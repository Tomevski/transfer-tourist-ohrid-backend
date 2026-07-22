package com.transfertourist.service;

import com.transfertourist.entity.Location;
import com.transfertourist.exception.BusinessRuleException;
import com.transfertourist.exception.ResourceNotFoundException;
import com.transfertourist.mapper.LocationMapper;
import com.transfertourist.repository.BookingRepository;
import com.transfertourist.repository.LocationRepository;
import com.transfertourist.repository.TransferPriceRepository;
import com.transfertourist.util.Slugifier;
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
 * Unit tests for {@link LocationService#delete} — the admin delete guard that
 * refuses to remove a location still referenced by a transfer price or a booking
 * leg (deactivate instead). Both {@code exists} checks are evaluated eagerly, so
 * an unstubbed check simply returns {@code false} (Mockito default).
 */
@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    private static final String ID = "loc-1";

    @Mock private LocationRepository locationRepository;
    @Mock private TransferPriceRepository transferPriceRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private LocationMapper locationMapper;
    @Mock private Slugifier slugifier;

    private LocationService service() {
        return new LocationService(locationRepository, transferPriceRepository,
                bookingRepository, locationMapper, slugifier);
    }

    @Test
    void deleteRemovesLocationWhenNotReferenced() {
        Location location = new Location();
        when(locationRepository.findById(ID)).thenReturn(Optional.of(location));

        service().delete(ID);

        verify(locationRepository).delete(location);
    }

    @Test
    void deleteRejectsLocationUsedByTransferPrice() {
        when(locationRepository.findById(ID)).thenReturn(Optional.of(new Location()));
        when(transferPriceRepository.existsByFromLocation_IdOrToLocation_Id(ID, ID)).thenReturn(true);

        assertThatExceptionOfType(BusinessRuleException.class)
                .isThrownBy(() -> service().delete(ID));

        verify(locationRepository, never()).delete(any());
    }

    @Test
    void deleteRejectsLocationUsedByBookingLeg() {
        when(locationRepository.findById(ID)).thenReturn(Optional.of(new Location()));
        when(bookingRepository.existsByAnyLegLocation(ID)).thenReturn(true);

        assertThatExceptionOfType(BusinessRuleException.class)
                .isThrownBy(() -> service().delete(ID));

        verify(locationRepository, never()).delete(any());
    }

    @Test
    void deleteRejectsUnknownLocation() {
        when(locationRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> service().delete("missing"));

        verify(locationRepository, never()).delete(any());
    }
}
