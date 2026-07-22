package com.transfertourist.event;

/**
 * Published by {@code ContactService.submit()} after the message is persisted.
 * Consumed after transaction commit to email both the operator (the message,
 * reply-to = sender) and the sender (an acknowledgement, reply-to = operator).
 * Carries a fully-resolved {@link ContactEmailPayload} so the listener does no
 * DB access.
 */
public record ContactMessageReceivedEvent(ContactEmailPayload contact) {
}
