package com.transfertourist.service;

import com.transfertourist.dto.request.ContactMessageRequest;
import com.transfertourist.dto.response.ContactAckResponse;
import com.transfertourist.entity.ContactMessage;
import com.transfertourist.repository.ContactMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Persists Contact Us submissions. Milestone 2.6 adds the async operator email;
 * for now the message is stored and acknowledged.
 */
@Service
public class ContactService {

    private final ContactMessageRepository contactMessageRepository;

    public ContactService(ContactMessageRepository contactMessageRepository) {
        this.contactMessageRepository = contactMessageRepository;
    }

    @Transactional
    public ContactAckResponse submit(ContactMessageRequest request) {
        ContactMessage message = new ContactMessage();
        message.setId("cmsg-" + UUID.randomUUID());
        message.setName(request.name());
        message.setSurname(request.surname());
        message.setEmail(request.email());
        message.setMessage(request.message());
        message.setCreatedAt(Instant.now());
        message.setHandled(false);
        contactMessageRepository.save(message);
        return new ContactAckResponse(true);
    }
}
