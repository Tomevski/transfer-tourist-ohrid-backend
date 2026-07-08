package com.transfertourist.mapper;

import com.transfertourist.dto.response.TransferPriceResponse;
import com.transfertourist.entity.TransferPrice;
import org.springframework.stereotype.Component;

/** Converts {@link TransferPrice} entities to their public DTO (ids, not nested objects). */
@Component
public class TransferPriceMapper {

    public TransferPriceResponse toResponse(TransferPrice tp) {
        return new TransferPriceResponse(
                tp.getId(),
                tp.getFromLocation().getId(),
                tp.getToLocation().getId(),
                tp.getVehicle().getId(),
                tp.getPrice()
        );
    }
}
