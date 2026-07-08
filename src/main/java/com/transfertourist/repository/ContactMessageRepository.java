package com.transfertourist.repository;

import com.transfertourist.entity.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, String> {
}
