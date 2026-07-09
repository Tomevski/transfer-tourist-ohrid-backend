package com.transfertourist.dto.response;

import com.transfertourist.constants.BookingStatus;

import java.math.BigDecimal;
import java.util.List;

/**
 * Aggregated admin dashboard payload (mirrors the frontend {@code StatSummary}).
 * Money fields are {@link BigDecimal} (serialized as JSON numbers); counts are
 * whole numbers.
 */
public record StatSummaryResponse(
        long totalRequests,
        long pending,
        long confirmed,
        long declined,
        BigDecimal revenue,
        BigDecimal revenueThisMonth,
        BigDecimal revenueLastMonth,
        List<MonthlyPoint> monthlyTrends,
        List<CountItem> popularRoutes,
        List<CountItem> topVehicles,
        List<StatusCount> statusBreakdown
) {

    /** One point in the last-6-months trend line. */
    public record MonthlyPoint(String month, String label, long bookings, BigDecimal revenue) {
    }

    /** A labelled tally (popular route or top vehicle). */
    public record CountItem(String label, long count) {
    }

    /** Booking count for a single status. */
    public record StatusCount(BookingStatus status, long count) {
    }
}
