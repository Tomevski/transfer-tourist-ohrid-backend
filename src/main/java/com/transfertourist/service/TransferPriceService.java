package com.transfertourist.service;

import com.transfertourist.dto.response.TransferPriceResponse;
import com.transfertourist.mapper.TransferPriceMapper;
import com.transfertourist.repository.TransferPriceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Read access to route pricing for the public booking form. */
@Service
@Transactional(readOnly = true)
public class TransferPriceService {

    private final TransferPriceRepository transferPriceRepository;
    private final TransferPriceMapper transferPriceMapper;

    public TransferPriceService(TransferPriceRepository transferPriceRepository,
                                TransferPriceMapper transferPriceMapper) {
        this.transferPriceRepository = transferPriceRepository;
        this.transferPriceMapper = transferPriceMapper;
    }

    /** Transfer prices (one per priced vehicle) defined for a route direction. */
    public List<TransferPriceResponse> byRoute(String fromLocationId, String toLocationId) {
        return transferPriceRepository
                .findByFromLocation_IdAndToLocation_Id(fromLocationId, toLocationId)
                .stream()
                .map(transferPriceMapper::toResponse)
                .toList();
    }
}
