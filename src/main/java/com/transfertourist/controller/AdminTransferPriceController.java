package com.transfertourist.controller;

import com.transfertourist.dto.response.TransferPriceResponse;
import com.transfertourist.service.TransferPriceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Admin transfer-price list (all rows). {@code ROLE_ADMIN} only. */
@RestController
@RequestMapping("/api/v1/admin/transfer-prices")
public class AdminTransferPriceController {

    private final TransferPriceService transferPriceService;

    public AdminTransferPriceController(TransferPriceService transferPriceService) {
        this.transferPriceService = transferPriceService;
    }

    @GetMapping
    public List<TransferPriceResponse> list() {
        return transferPriceService.adminList();
    }
}
