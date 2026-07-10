package com.transfertourist.listener;

import com.transfertourist.event.BookingConfirmedEvent;
import com.transfertourist.event.BookingCreatedEvent;
import com.transfertourist.event.BookingDeclinedEvent;
import com.transfertourist.service.EmailService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Sends booking emails in reaction to domain events.
 *
 * <p>Each handler is {@link TransactionalEventListener} at
 * {@link TransactionPhase#AFTER_COMMIT} + {@link Async} on the shared
 * {@code taskExecutor}, which gives three guarantees:
 * <ul>
 *   <li><b>No email on rollback</b> — the listener runs only after the booking
 *       transaction commits successfully.</li>
 *   <li><b>Non-blocking</b> — mail I/O runs off the request thread, so the API
 *       response is never delayed by SMTP.</li>
 *   <li><b>Isolated failures</b> — the booking is already committed and the
 *       {@link EmailService} swallows send failures, so email problems can never
 *       roll back or corrupt a booking.</li>
 * </ul>
 * The event payloads are fully resolved, so no DB access or lazy navigation
 * happens here.
 */
@Component
public class BookingEmailListener {

    private final EmailService emailService;

    public BookingEmailListener(EmailService emailService) {
        this.emailService = emailService;
    }

    /** New booking → operator (full detail) + customer (acknowledgement). */
    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBookingCreated(BookingCreatedEvent event) {
        // Independent sends: each is self-contained/retried so one failing does
        // not stop the other from going out.
        emailService.sendOperatorNewBooking(event.booking());
        emailService.sendCustomerAcknowledgement(event.booking());
    }

    /** Admin confirmed → customer confirmation. */
    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBookingConfirmed(BookingConfirmedEvent event) {
        emailService.sendCustomerConfirmation(event.booking());
    }

    /** Admin declined → customer decline. */
    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBookingDeclined(BookingDeclinedEvent event) {
        emailService.sendCustomerDecline(event.booking());
    }
}
