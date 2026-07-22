package com.transfertourist.service;

import com.transfertourist.entity.Vehicle;
import com.transfertourist.exception.BusinessRuleException;
import com.transfertourist.exception.ResourceNotFoundException;
import com.transfertourist.mapper.VehicleMapper;
import com.transfertourist.repository.BookingRepository;
import com.transfertourist.repository.TransferPriceRepository;
import com.transfertourist.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link VehicleService#delete} — the admin delete guard that
 * refuses to remove a vehicle still used by a booking or a transfer price
 * (deactivate instead). The guard uses {@code ||} short-circuit, so the
 * transfer-price check is only reached when no booking uses the vehicle; the
 * booking-referenced test therefore does not stub it.
 */
@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    private static final String ID = "veh-1";

    @Mock private VehicleRepository vehicleRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private TransferPriceRepository transferPriceRepository;
    @Mock private VehicleMapper vehicleMapper;

    private VehicleService service() {
        return new VehicleService(vehicleRepository, bookingRepository,
                transferPriceRepository, vehicleMapper);
    }

    @Test
    void deleteRemovesVehicleWhenNotReferenced() {
        Vehicle vehicle = new Vehicle();
        when(vehicleRepository.findById(ID)).thenReturn(Optional.of(vehicle));

        service().delete(ID);

        verify(vehicleRepository).delete(vehicle);
    }

    @Test
    void deleteRejectsVehicleUsedByBooking() {
        when(vehicleRepository.findById(ID)).thenReturn(Optional.of(new Vehicle()));
        when(bookingRepository.existsByVehicle_Id(ID)).thenReturn(true);

        assertThatExceptionOfType(BusinessRuleException.class)
                .isThrownBy(() -> service().delete(ID));

        verify(vehicleRepository, never()).delete(any());
    }

    @Test
    void deleteRejectsVehicleUsedByTransferPrice() {
        when(vehicleRepository.findById(ID)).thenReturn(Optional.of(new Vehicle()));
        // No booking uses it, so the || falls through to the transfer-price check.
        when(transferPriceRepository.existsByVehicle_Id(ID)).thenReturn(true);

        assertThatExceptionOfType(BusinessRuleException.class)
                .isThrownBy(() -> service().delete(ID));

        verify(vehicleRepository, never()).delete(any());
    }

    @Test
    void deleteRejectsUnknownVehicle() {
        when(vehicleRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> service().delete("missing"));

        verify(vehicleRepository, never()).delete(any());
    }
}
