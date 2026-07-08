package com.transfertourist.controller;

import com.transfertourist.dto.response.TransferPriceResponse;
import com.transfertourist.service.TransferPriceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Public transfer-price lookup for the booking form. Admin CRUD arrives in
 * Milestone 2.5.
 */
@RestController
@RequestMapping("/api/v1/transfer-prices")
public class TransferPriceController {

    private final TransferPriceService transferPriceService;

    public TransferPriceController(TransferPriceService transferPriceService) {
        this.transferPriceService = transferPriceService;
    }

    /** Transfer prices (per vehicle) available for a route direction. */
    @GetMapping
    public List<TransferPriceResponse> byRoute(@RequestParam("from") String from,
                                               @RequestParam("to") String to) {
        return transferPriceService.byRoute(from, to);
    }
}
