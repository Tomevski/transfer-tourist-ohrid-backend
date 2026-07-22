package com.transfertourist.service;

import com.transfertourist.event.BookingEmailPayload;
import com.transfertourist.event.ContactEmailPayload;

/**
 * Renders and sends the transactional booking emails (Milestone 2.6). All
 * methods take a fully-resolved {@link BookingEmailPayload} and perform no DB
 * access, so they are safe to call from an async, post-commit context. A send
 * failure is retried and ultimately logged — never rethrown — so it can never
 * affect the booking that triggered it.
 */
public interface EmailService {

    /** To the operator: every booking + customer detail. Reply-to = customer. */
    void sendOperatorNewBooking(BookingEmailPayload booking);

    /** To the customer: acknowledgement that the request was received (pending). */
    void sendCustomerAcknowledgement(BookingEmailPayload booking);

    /** To the customer: the operator confirmed the booking. */
    void sendCustomerConfirmation(BookingEmailPayload booking);

    /** To the customer: the operator declined the booking. */
    void sendCustomerDecline(BookingEmailPayload booking);

    /** To the operator: a new Contact Us message. Reply-to = the sender. */
    void sendOperatorContactMessage(ContactEmailPayload contact);

    /** To the sender: acknowledgement that their message was received. Reply-to = operator. */
    void sendContactAcknowledgement(ContactEmailPayload contact);
}
