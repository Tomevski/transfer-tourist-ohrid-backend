package com.transfertourist.service.impl;

import com.transfertourist.event.BookingEmailPayload;
import com.transfertourist.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Thymeleaf + {@link JavaMailSender} implementation of {@link EmailService}.
 *
 * <p>Each method renders an HTML template from {@code templates/email/} against
 * the immutable payload and sends it. Sending is wrapped in a small bounded
 * retry: transient SMTP failures are retried a few times, and a final failure is
 * logged at {@code ERROR} but swallowed — the caller (an async, post-commit
 * listener) must never see an exception, so a mail outage can never roll back or
 * otherwise disturb a booking.
 */
@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);
    private static final int MAX_ATTEMPTS = 3;
    private static final long RETRY_BACKOFF_MS = 500L;

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final String fromAddress;
    private final String operatorAddress;

    // JavaMailSender is provided by Spring Boot's MailSenderAutoConfiguration when
    // spring.mail.host is set (every profile sets it). IntelliJ can't evaluate that
    // @ConditionalOnProperty, so it wrongly flags the injection point.
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public EmailServiceImpl(JavaMailSender mailSender,
                            SpringTemplateEngine templateEngine,
                            @Value("${app.mail.from}") String fromAddress,
                            @Value("${app.mail.operator}") String operatorAddress) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.fromAddress = fromAddress;
        this.operatorAddress = operatorAddress;
    }

    @Override
    public void sendOperatorNewBooking(BookingEmailPayload booking) {
        // Operator email: reply-to the customer so a reply reaches the traveller.
        send("email/operator-new-booking", context(booking), operatorAddress,
                "New booking request " + booking.referenceCode(),
                booking.customer().email(), booking.referenceCode());
    }

    @Override
    public void sendCustomerAcknowledgement(BookingEmailPayload booking) {
        send("email/customer-acknowledgement", context(booking), booking.customer().email(),
                "We received your transfer request " + booking.referenceCode(),
                operatorAddress, booking.referenceCode());
    }

    @Override
    public void sendCustomerConfirmation(BookingEmailPayload booking) {
        send("email/customer-confirm", context(booking), booking.customer().email(),
                "Your transfer is confirmed · " + booking.referenceCode(),
                operatorAddress, booking.referenceCode());
    }

    @Override
    public void sendCustomerDecline(BookingEmailPayload booking) {
        send("email/customer-decline", context(booking), booking.customer().email(),
                "Update on your transfer request " + booking.referenceCode(),
                operatorAddress, booking.referenceCode());
    }

    private Context context(BookingEmailPayload booking) {
        Context ctx = new Context(Locale.ENGLISH);
        ctx.setVariable("booking", booking);
        return ctx;
    }

    /**
     * Renders the template and sends the message, retrying transient failures.
     * Never throws: a final failure is logged and swallowed.
     *
     * @param referenceCode carried only for log correlation
     */
    private void send(String template, Context context, String to, String subject,
                      String replyTo, String referenceCode) {
        String html = templateEngine.process(template, context);

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper =
                        new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
                helper.setFrom(fromAddress);
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(html, true); // true = HTML body
                if (replyTo != null && !replyTo.isBlank()) {
                    helper.setReplyTo(replyTo);
                }
                mailSender.send(message);
                log.info("Sent '{}' email to {} (booking {})", template, to, referenceCode);
                return;
            } catch (MailException | jakarta.mail.MessagingException ex) {
                log.warn("Attempt {}/{} to send '{}' to {} (booking {}) failed: {}",
                        attempt, MAX_ATTEMPTS, template, to, referenceCode, ex.getMessage());
                if (attempt < MAX_ATTEMPTS) {
                    sleepBeforeRetry(attempt);
                }
            }
        }
        log.error("Giving up: could not send '{}' email to {} (booking {}) after {} attempts",
                template, to, referenceCode, MAX_ATTEMPTS);
    }

    private void sleepBeforeRetry(int attempt) {
        try {
            Thread.sleep(RETRY_BACKOFF_MS * attempt);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}
