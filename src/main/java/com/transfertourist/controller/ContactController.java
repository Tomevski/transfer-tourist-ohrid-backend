package com.transfertourist.controller;

import com.transfertourist.dto.request.ContactMessageRequest;
import com.transfertourist.dto.response.ContactAckResponse;
import com.transfertourist.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Public Contact Us submission. Admin message listing arrives later (Phase 3). */
@RestController
@RequestMapping("/api/v1/contact")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ContactAckResponse submit(@Valid @RequestBody ContactMessageRequest request) {
        return contactService.submit(request);
    }
}
