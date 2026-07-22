package com.transfertourist.service;

import com.transfertourist.dto.request.ContactMessageRequest;
import com.transfertourist.dto.response.ContactAckResponse;
import com.transfertourist.entity.ContactMessage;
import com.transfertourist.event.ContactEmailPayload;
import com.transfertourist.event.ContactMessageReceivedEvent;
import com.transfertourist.repository.ContactMessageRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

/**
 * Persists Contact Us submissions and (Milestone 2.6) fires the notification
 * emails: one to the operator with the message and one acknowledgement to the
 * sender. Emails are published as a {@link ContactMessageReceivedEvent} and sent
 * async after commit, so a mail failure can never affect the stored message.
 */
@Service
public class ContactService {

    private static final DateTimeFormatter RECEIVED_AT_FORMAT =
            DateTimeFormatter.ofPattern("EEE, d MMM yyyy 'at' HH:mm 'UTC'", Locale.ENGLISH);

    private final ContactMessageRepository contactMessageRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ContactService(ContactMessageRepository contactMessageRepository,
                          ApplicationEventPublisher eventPublisher) {
        this.contactMessageRepository = contactMessageRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ContactAckResponse submit(ContactMessageRequest request) {
        Instant now = Instant.now();

        ContactMessage message = new ContactMessage();
        message.setId("cmsg-" + UUID.randomUUID());
        message.setName(request.name());
        message.setSurname(request.surname());
        message.setEmail(request.email());
        message.setMessage(request.message());
        message.setCreatedAt(now);
        message.setHandled(false);
        contactMessageRepository.save(message);

        // Payload fully resolved here; the listener sends after commit, off-thread.
        eventPublisher.publishEvent(new ContactMessageReceivedEvent(
                new ContactEmailPayload(
                        request.name() + " " + request.surname(),
                        request.name(),
                        request.email(),
                        request.message(),
                        RECEIVED_AT_FORMAT.format(now.atZone(ZoneOffset.UTC))
                )));

        return new ContactAckResponse(true);
    }
}
