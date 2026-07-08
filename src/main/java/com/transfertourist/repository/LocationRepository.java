package com.transfertourist.repository;

import com.transfertourist.constants.LocationCategory;
import com.transfertourist.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, String> {

    List<Location> findByActiveTrueOrderBySortOrderAsc();

    List<Location> findByCategoryAndActiveTrueOrderBySortOrderAsc(LocationCategory category);

    Optional<Location> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
