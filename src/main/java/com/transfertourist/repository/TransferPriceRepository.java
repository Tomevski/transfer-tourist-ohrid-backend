package com.transfertourist.repository;

import com.transfertourist.entity.TransferPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransferPriceRepository extends JpaRepository<TransferPrice, String> {

    /** All priced vehicles for a route, used to build the vehicle selector. */
    List<TransferPrice> findByFromLocation_IdAndToLocation_Id(String fromLocationId, String toLocationId);

    Optional<TransferPrice> findByFromLocation_IdAndToLocation_IdAndVehicle_Id(
            String fromLocationId, String toLocationId, String vehicleId);

    boolean existsByFromLocation_IdAndToLocation_IdAndVehicle_Id(
            String fromLocationId, String toLocationId, String vehicleId);

    /** True if the location is on either side of any transfer price (delete guard). */
    boolean existsByFromLocation_IdOrToLocation_Id(String fromLocationId, String toLocationId);

    /** True if any transfer price uses the given vehicle (delete guard). */
    boolean existsByVehicle_Id(String vehicleId);
}
