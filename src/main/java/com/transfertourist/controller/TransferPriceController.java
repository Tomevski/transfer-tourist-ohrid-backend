package com.transfertourist.controller;

import com.transfertourist.dto.common.IdResponse;
import com.transfertourist.dto.request.TransferPriceRequest;
import com.transfertourist.dto.response.TransferPriceResponse;
import com.transfertourist.service.TransferPriceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Transfer prices: public route lookup plus admin mutations. The admin list is
 * served from {@code /admin/transfer-prices} (see {@link AdminTransferPriceController}).
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransferPriceResponse create(@Valid @RequestBody TransferPriceRequest request) {
        return transferPriceService.create(request);
    }

    @PutMapping("/{id}")
    public TransferPriceResponse update(@PathVariable String id, @Valid @RequestBody TransferPriceRequest request) {
        return transferPriceService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public IdResponse delete(@PathVariable String id) {
        transferPriceService.delete(id);
        return new IdResponse(id);
    }
}
