package com.transfertourist.validation;

import com.transfertourist.constants.TripType;
import com.transfertourist.dto.common.LegDto;
import com.transfertourist.dto.request.BookingCreateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

/**
 * Server-side mirror of the frontend {@code bookingSchema.superRefine} (CR-2).
 * Adds per-field constraint violations (dotted paths like {@code returnLeg.date})
 * so {@code GlobalExceptionHandler} surfaces them in {@code fieldErrors}.
 */
public class BookingValidator implements ConstraintValidator<ValidBooking, BookingCreateRequest> {

    @Override
    public boolean isValid(BookingCreateRequest req, ConstraintValidatorContext ctx) {
        if (req == null) {
            return true; // null-ness handled elsewhere
        }
        ctx.disableDefaultConstraintViolation();
        boolean valid = refineLeg(req.outbound(), ctx, "outbound");

        if (req.tripType() == TripType.RETURN) {
            valid &= refineLeg(req.returnLeg(), ctx, "returnLeg");
            valid &= validateReturnOrdering(req.outbound(), req.returnLeg(), ctx);
        }
        return valid;
    }

    /** Required fields + from ≠ to for a single leg, conditional on its purpose. */
    private boolean refineLeg(LegDto leg, ConstraintValidatorContext ctx, String prefix) {
        if (leg == null) {
            addError(ctx, "Select a pickup location", prefix, "fromLocationId");
            return false;
        }
        boolean valid = true;
        valid &= requireText(leg.fromLocationId(), ctx, "Select a pickup location", prefix, "fromLocationId");
        valid &= requireText(leg.toLocationId(), ctx, "Select a destination", prefix, "toLocationId");
        if (leg.date() == null) {
            addError(ctx, "Select a date", prefix, "date");
            valid = false;
        }

        if (isPresent(leg.fromLocationId()) && isPresent(leg.toLocationId())
                && leg.fromLocationId().equals(leg.toLocationId())) {
            addError(ctx, "Destination must be different from pickup", prefix, "toLocationId");
            valid = false;
        }

        if (leg.purpose() != null) {
            switch (leg.purpose()) {
                case ARRIVING -> {
                    valid &= requireText(leg.flightNumber(), ctx, "Enter your flight number", prefix, "flightNumber");
                    valid &= requireText(leg.flightTime(), ctx, "Enter the flight arrival time", prefix, "flightTime");
                }
                case DEPARTING -> {
                    valid &= requireText(leg.pickupTime(), ctx, "Enter the pickup time", prefix, "pickupTime");
                    valid &= requireText(leg.flightTime(), ctx, "Enter the flight departure time", prefix, "flightTime");
                }
                case NOT_FLYING ->
                        valid &= requireText(leg.pickupTime(), ctx, "Enter the pickup time", prefix, "pickupTime");
            }
        }
        return valid;
    }

    /** Return date must be on/after outbound; same day, its time must not be earlier. */
    private boolean validateReturnOrdering(LegDto outbound, LegDto returnLeg, ConstraintValidatorContext ctx) {
        if (outbound == null || returnLeg == null) {
            return true;
        }
        LocalDate out = outbound.date();
        LocalDate ret = returnLeg.date();
        if (out == null || ret == null) {
            return true;
        }
        if (ret.isBefore(out)) {
            addError(ctx, "Return date must be on or after the outbound date", "returnLeg", "date");
            return false;
        }
        if (ret.isEqual(out)) {
            String outTime = legTime(outbound);
            String retTime = legTime(returnLeg);
            if (isPresent(outTime) && isPresent(retTime) && retTime.compareTo(outTime) < 0) {
                addError(ctx, "Return time must be after the outbound time on the same day", "returnLeg", "date");
                return false;
            }
        }
        return true;
    }

    /** The time-of-day a leg departs, for ordering (pickup preferred, else flight). */
    private static String legTime(LegDto leg) {
        if (isPresent(leg.pickupTime())) {
            return leg.pickupTime();
        }
        return isPresent(leg.flightTime()) ? leg.flightTime() : "";
    }

    private boolean requireText(String value, ConstraintValidatorContext ctx, String message, String... path) {
        if (!isPresent(value)) {
            addError(ctx, message, path);
            return false;
        }
        return true;
    }

    private static boolean isPresent(String value) {
        return value != null && !value.isBlank();
    }

    /** Builds a violation whose property path is the dotted {@code path} segments. */
    private void addError(ConstraintValidatorContext ctx, String message, String... path) {
        ConstraintValidatorContext.ConstraintViolationBuilder builder =
                ctx.buildConstraintViolationWithTemplate(message);
        ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext node =
                builder.addPropertyNode(path[0]);
        for (int i = 1; i < path.length; i++) {
            node = node.addPropertyNode(path[i]);
        }
        node.addConstraintViolation();
    }
}
