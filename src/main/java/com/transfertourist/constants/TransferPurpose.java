package com.transfertourist.constants;

/**
 * Purpose of a single leg, which drives its conditional flight/pickup fields:
 * ARRIVING → flight arrival time + number, DEPARTING → pickup + departure time,
 * NOT_FLYING → pickup time only.
 */
public enum TransferPurpose {
    ARRIVING,
    DEPARTING,
    NOT_FLYING
}
