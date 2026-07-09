package com.transfertourist.repository;

import com.transfertourist.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, String> {

    List<Vehicle> findByActiveTrueOrderBySortOrderAsc();

    /** Admin list: every vehicle (incl. inactive), ordered by sort order. */
    List<Vehicle> findAllByOrderBySortOrderAsc();
}
