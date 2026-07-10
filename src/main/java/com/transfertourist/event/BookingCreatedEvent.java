package com.transfertourist.event;

/**
 * Published by {@code BookingService.create()} after the booking is persisted.
 * Consumed after transaction commit to email both the operator (full details)
 * and the customer (acknowledgement). Carries a fully-resolved
 * {@link BookingEmailPayload} so the listener does no DB access.
 */
public record BookingCreatedEvent(BookingEmailPayload booking) {
}
