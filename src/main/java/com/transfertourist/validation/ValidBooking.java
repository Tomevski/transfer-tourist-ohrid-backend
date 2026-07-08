package com.transfertourist.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class-level constraint for a booking request. Enforces the cross-field rules
 * that depend on trip type and transfer purpose (CR-2): per-leg from ≠ to,
 * conditional flight/pickup requiredness, and return date/time ≥ outbound. The
 * validator emits per-field messages so the SPA can attach them to fields.
 */
@Documented
@Constraint(validatedBy = BookingValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBooking {

    String message() default "Invalid booking";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
