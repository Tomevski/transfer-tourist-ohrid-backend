package com.transfertourist.event;

/**
 * Published by {@code BookingService.confirm()} after the status transition.
 * Consumed after commit to send the customer a confirmation email.
 */
public record BookingConfirmedEvent(BookingEmailPayload booking) {
}
