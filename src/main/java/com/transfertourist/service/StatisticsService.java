package com.transfertourist.service;

import com.transfertourist.constants.BookingStatus;
import com.transfertourist.dto.response.StatSummaryResponse;
import com.transfertourist.entity.Booking;
import com.transfertourist.entity.Location;
import com.transfertourist.repository.BookingRepository;
import com.transfertourist.repository.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Computes the admin dashboard statistics (CR-4), mirroring the frontend mock:
 * status tallies, confirmed-booking revenue (total / this month / last month), a
 * 6-month trend line, and top-5 routes and vehicles. The booking set is small, so
 * everything is aggregated in memory in one read.
 */
@Service
@Transactional(readOnly = true)
public class StatisticsService {

    private static final int TREND_MONTHS = 6;
    private static final int TOP_LIMIT = 5;
    private static final String ROUTE_ARROW = " → "; // " → "

    private final BookingRepository bookingRepository;
    private final LocationRepository locationRepository;

    public StatisticsService(BookingRepository bookingRepository, LocationRepository locationRepository) {
        this.bookingRepository = bookingRepository;
        this.locationRepository = locationRepository;
    }

    public StatSummaryResponse summary() {
        // Newest-first so tie-breaking in the top-N tallies matches the mock.
        List<Booking> bookings = bookingRepository.findAll().stream()
                .sorted(Comparator.comparing(Booking::getCreatedAt).reversed())
                .toList();
        Map<String, String> locationNames = locationRepository.findAll().stream()
                .collect(Collectors.toMap(Location::getId, Location::getName));

        long pending = countByStatus(bookings, BookingStatus.PENDING);
        long confirmed = countByStatus(bookings, BookingStatus.CONFIRMED);
        long declined = countByStatus(bookings, BookingStatus.DECLINED);

        YearMonth thisMonth = YearMonth.now(ZoneOffset.UTC);
        YearMonth lastMonth = thisMonth.minusMonths(1);

        BigDecimal revenue = confirmedRevenue(bookings, null);
        BigDecimal revenueThisMonth = confirmedRevenue(bookings, thisMonth);
        BigDecimal revenueLastMonth = confirmedRevenue(bookings, lastMonth);

        List<StatSummaryResponse.MonthlyPoint> monthlyTrends = monthlyTrends(bookings, thisMonth);
        List<StatSummaryResponse.CountItem> popularRoutes = topCounts(
                bookings.stream().map(b -> routeLabel(b, locationNames)));
        List<StatSummaryResponse.CountItem> topVehicles = topCounts(
                bookings.stream().map(b -> b.getVehicle().getName()));
        List<StatSummaryResponse.StatusCount> statusBreakdown = List.of(
                new StatSummaryResponse.StatusCount(BookingStatus.PENDING, pending),
                new StatSummaryResponse.StatusCount(BookingStatus.CONFIRMED, confirmed),
                new StatSummaryResponse.StatusCount(BookingStatus.DECLINED, declined));

        return new StatSummaryResponse(
                bookings.size(), pending, confirmed, declined,
                revenue, revenueThisMonth, revenueLastMonth,
                monthlyTrends, popularRoutes, topVehicles, statusBreakdown);
    }

    private long countByStatus(List<Booking> bookings, BookingStatus status) {
        return bookings.stream().filter(b -> b.getStatus() == status).count();
    }

    /** Sum of confirmed-booking totals, optionally restricted to a single month. */
    private BigDecimal confirmedRevenue(List<Booking> bookings, YearMonth month) {
        return bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .filter(b -> month == null || month.equals(monthOf(b)))
                .map(Booking::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<StatSummaryResponse.MonthlyPoint> monthlyTrends(List<Booking> bookings, YearMonth thisMonth) {
        return java.util.stream.IntStream.range(0, TREND_MONTHS)
                .mapToObj(i -> thisMonth.minusMonths(TREND_MONTHS - 1L - i))
                .map(ym -> {
                    long count = bookings.stream().filter(b -> ym.equals(monthOf(b))).count();
                    BigDecimal revenue = confirmedRevenue(bookings, ym);
                    String label = ym.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                    return new StatSummaryResponse.MonthlyPoint(ym.toString(), label, count, revenue);
                })
                .toList();
    }

    private String routeLabel(Booking booking, Map<String, String> locationNames) {
        String from = locationNames.getOrDefault(booking.getOutbound().getFromLocationId(),
                booking.getOutbound().getFromLocationId());
        String to = locationNames.getOrDefault(booking.getOutbound().getToLocationId(),
                booking.getOutbound().getToLocationId());
        return from + ROUTE_ARROW + to;
    }

    /** Counts occurrences of each label and returns the top {@value #TOP_LIMIT}, most frequent first. */
    private List<StatSummaryResponse.CountItem> topCounts(java.util.stream.Stream<String> labels) {
        Map<String, Long> counts = labels.collect(Collectors.groupingBy(
                Function.identity(), LinkedHashMap::new, Collectors.counting()));
        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(TOP_LIMIT)
                .map(e -> new StatSummaryResponse.CountItem(e.getKey(), e.getValue()))
                .toList();
    }

    private YearMonth monthOf(Booking booking) {
        return YearMonth.from(booking.getCreatedAt().atZone(ZoneOffset.UTC));
    }
}
