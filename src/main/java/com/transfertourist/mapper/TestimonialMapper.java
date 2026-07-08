package com.transfertourist.mapper;

import com.transfertourist.dto.response.TestimonialResponse;
import com.transfertourist.entity.Testimonial;
import org.springframework.stereotype.Component;

/** Converts {@link Testimonial} entities to their public DTO. */
@Component
public class TestimonialMapper {

    public TestimonialResponse toResponse(Testimonial t) {
        return new TestimonialResponse(
                t.getId(),
                t.getAuthorName(),
                t.getLocation(),
                t.getCountry(),
                t.getRating(),
                t.getContent(),
                t.getCreatedAt(),
                t.isPublished()
        );
    }
}
