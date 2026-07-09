package com.transfertourist.repository;

import com.transfertourist.constants.BookingStatus;
import com.transfertourist.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, String> {

    Optional<Booking> findByReferenceCode(String referenceCode);

    boolean existsByReferenceCode(String referenceCode);

    List<Booking> findByStatusOrderByCreatedAtDesc(BookingStatus status);

    /**
     * Admin search: optional exact {@code status} (null = all) and a
     * case-insensitive free-text {@code q} over reference code, customer
     * name/email and the outbound route's location names ({@code q} must be
     * pre-lowercased; pass {@code ""} to skip). Mirrors the mock's booking list.
     */
    @Query(value = """
            SELECT b FROM Booking b
              LEFT JOIN Location lf ON lf.id = b.outbound.fromLocationId
              LEFT JOIN Location lt ON lt.id = b.outbound.toLocationId
            WHERE (:status IS NULL OR b.status = :status)
              AND (:q = ''
                   OR LOWER(b.referenceCode)        LIKE CONCAT('%', :q, '%')
                   OR LOWER(b.customer.firstName)   LIKE CONCAT('%', :q, '%')
                   OR LOWER(b.customer.lastName)    LIKE CONCAT('%', :q, '%')
                   OR LOWER(b.customer.email)       LIKE CONCAT('%', :q, '%')
                   OR LOWER(lf.name)                LIKE CONCAT('%', :q, '%')
                   OR LOWER(lt.name)                LIKE CONCAT('%', :q, '%'))
            """,
            countQuery = """
            SELECT COUNT(b) FROM Booking b
              LEFT JOIN Location lf ON lf.id = b.outbound.fromLocationId
              LEFT JOIN Location lt ON lt.id = b.outbound.toLocationId
            WHERE (:status IS NULL OR b.status = :status)
              AND (:q = ''
                   OR LOWER(b.referenceCode)        LIKE CONCAT('%', :q, '%')
                   OR LOWER(b.customer.firstName)   LIKE CONCAT('%', :q, '%')
                   OR LOWER(b.customer.lastName)    LIKE CONCAT('%', :q, '%')
                   OR LOWER(b.customer.email)       LIKE CONCAT('%', :q, '%')
                   OR LOWER(lf.name)                LIKE CONCAT('%', :q, '%')
                   OR LOWER(lt.name)                LIKE CONCAT('%', :q, '%'))
            """)
    Page<Booking> search(@Param("status") BookingStatus status, @Param("q") String q, Pageable pageable);

    /** True if any booking leg (outbound or return) references the given location. */
    @Query("""
            SELECT COUNT(b) > 0 FROM Booking b
            WHERE b.outbound.fromLocationId = :locationId
               OR b.outbound.toLocationId = :locationId
               OR b.returnLeg.fromLocationId = :locationId
               OR b.returnLeg.toLocationId = :locationId
            """)
    boolean existsByAnyLegLocation(@Param("locationId") String locationId);

    /** True if any booking uses the given vehicle. */
    boolean existsByVehicle_Id(String vehicleId);
}
