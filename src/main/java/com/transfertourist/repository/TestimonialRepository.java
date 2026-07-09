package com.transfertourist.repository;

import com.transfertourist.entity.Testimonial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TestimonialRepository extends JpaRepository<Testimonial, String> {

    /** Admin list: every testimonial (incl. unpublished), newest first. */
    List<Testimonial> findAllByOrderByCreatedAtDesc();

    /**
     * Published testimonials, optionally filtered by exact rating and a
     * case-insensitive free-text match over author/content/location/country.
     * {@code q} must be pre-lowercased; pass {@code ""} to skip text filtering.
     */
    @Query("""
            SELECT t FROM Testimonial t
            WHERE t.published = true
              AND (:rating IS NULL OR t.rating = :rating)
              AND (:q = ''
                   OR LOWER(t.authorName) LIKE CONCAT('%', :q, '%')
                   OR LOWER(t.content)    LIKE CONCAT('%', :q, '%')
                   OR LOWER(t.location)   LIKE CONCAT('%', :q, '%')
                   OR LOWER(t.country)    LIKE CONCAT('%', :q, '%'))
            """)
    Page<Testimonial> search(@Param("q") String q, @Param("rating") Integer rating, Pageable pageable);
}
