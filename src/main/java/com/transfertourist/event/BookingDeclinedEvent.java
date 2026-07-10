package com.transfertourist.event;

/**
 * Published by {@code BookingService.decline()} after the status transition.
 * Consumed after commit to send the customer a polite decline email.
 */
public record BookingDeclinedEvent(BookingEmailPayload booking) {
}
