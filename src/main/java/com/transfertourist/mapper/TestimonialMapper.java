package com.transfertourist.mapper;

import com.transfertourist.dto.request.TestimonialRequest;
import com.transfertourist.dto.response.TestimonialResponse;
import com.transfertourist.entity.Testimonial;
import org.springframework.stereotype.Component;

/** Converts {@link Testimonial} entities to their public DTO. */
@Component
public class TestimonialMapper {

    /** Copies mutable fields from an admin request onto an entity ({@code id}/{@code createdAt} are managed by the service). */
    public void applyRequest(TestimonialRequest request, Testimonial testimonial) {
        testimonial.setAuthorName(request.authorName().trim());
        testimonial.setLocation(request.location());
        testimonial.setCountry(request.country());
        testimonial.setRating(request.rating());
        testimonial.setContent(request.content());
        testimonial.setPublished(request.published());
    }

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
