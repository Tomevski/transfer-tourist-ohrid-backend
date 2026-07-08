package com.transfertourist.dto.response;

/** Acknowledgement returned after a contact message is accepted ({@code { received: true }}). */
public record ContactAckResponse(boolean received) {
}
