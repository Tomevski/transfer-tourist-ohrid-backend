package com.transfertourist.repository;

import com.transfertourist.constants.BookingStatus;
import com.transfertourist.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, String> {

    Optional<Booking> findByReferenceCode(String referenceCode);

    boolean existsByReferenceCode(String referenceCode);

    List<Booking> findByStatusOrderByCreatedAtDesc(BookingStatus status);
}
