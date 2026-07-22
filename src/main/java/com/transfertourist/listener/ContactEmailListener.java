package com.transfertourist.listener;

import com.transfertourist.event.ContactMessageReceivedEvent;
import com.transfertourist.service.EmailService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Sends the Contact Us emails in reaction to {@link ContactMessageReceivedEvent}.
 *
 * <p>Same guarantees as {@link BookingEmailListener}:
 * {@link TransactionalEventListener} at {@link TransactionPhase#AFTER_COMMIT}
 * (no email if the message wasn't persisted) + {@link Async} on the shared
 * {@code taskExecutor} (mail I/O off the request thread; the {@link EmailService}
 * swallows send failures so they can never disturb the persisted message).
 * The event payload is fully resolved, so no DB access happens here.
 */
@Component
public class ContactEmailListener {

    private final EmailService emailService;

    public ContactEmailListener(EmailService emailService) {
        this.emailService = emailService;
    }

    /** New contact message → operator (the message) + sender (acknowledgement). */
    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onContactMessageReceived(ContactMessageReceivedEvent event) {
        // Independent sends: each is self-contained/retried so one failing does
        // not stop the other from going out.
        emailService.sendOperatorContactMessage(event.contact());
        emailService.sendContactAcknowledgement(event.contact());
    }
}
