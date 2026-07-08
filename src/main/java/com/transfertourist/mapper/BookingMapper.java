package com.transfertourist.mapper;

import com.transfertourist.dto.common.CustomerDto;
import com.transfertourist.dto.common.LegDto;
import com.transfertourist.dto.response.BookingResponse;
import com.transfertourist.entity.Booking;
import com.transfertourist.entity.Customer;
import com.transfertourist.entity.Leg;
import org.springframework.stereotype.Component;

/** Converts between booking DTOs and the {@link Booking} entity graph. */
@Component
public class BookingMapper {

    public BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getReferenceCode(),
                booking.getStatus(),
                booking.getTripType(),
                booking.getPassengers(),
                booking.getInfantSeats(),
                booking.getComments(),
                booking.getVehicle().getId(),
                booking.getTotalPrice(),
                toLegDto(booking.getOutbound()),
                toLegDto(booking.getReturnLeg()),
                toCustomerDto(booking.getCustomer()),
                booking.getCreatedAt()
        );
    }

    public Leg toLegEntity(LegDto dto) {
        if (dto == null) {
            return null;
        }
        Leg leg = new Leg();
        leg.setFromLocationId(dto.fromLocationId());
        leg.setToLocationId(dto.toLocationId());
        leg.setDate(dto.date());
        leg.setPurpose(dto.purpose());
        leg.setFlightNumber(dto.flightNumber());
        leg.setFlightTime(dto.flightTime());
        leg.setPickupTime(dto.pickupTime());
        return leg;
    }

    public Customer toCustomerEntity(CustomerDto dto) {
        Customer customer = new Customer();
        customer.setFirstName(dto.firstName());
        customer.setLastName(dto.lastName());
        customer.setEmail(dto.email());
        customer.setPhone(dto.phone());
        return customer;
    }

    private LegDto toLegDto(Leg leg) {
        if (leg == null) {
            return null;
        }
        return new LegDto(
                leg.getFromLocationId(),
                leg.getToLocationId(),
                leg.getDate(),
                leg.getPurpose(),
                leg.getFlightNumber(),
                leg.getFlightTime(),
                leg.getPickupTime()
        );
    }

    private CustomerDto toCustomerDto(Customer customer) {
        return new CustomerDto(
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhone()
        );
    }
}
