package com.transfertourist.repository;

import com.transfertourist.entity.Testimonial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestimonialRepository extends JpaRepository<Testimonial, String> {

    // Paged/search queries are added in Milestone 2.3 (public read surface).
}
