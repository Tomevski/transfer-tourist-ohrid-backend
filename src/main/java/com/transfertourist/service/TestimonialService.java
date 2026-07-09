package com.transfertourist.service;

import com.transfertourist.dto.common.PageResponse;
import com.transfertourist.dto.request.TestimonialRequest;
import com.transfertourist.dto.response.TestimonialResponse;
import com.transfertourist.entity.Testimonial;
import com.transfertourist.exception.ResourceNotFoundException;
import com.transfertourist.mapper.TestimonialMapper;
import com.transfertourist.repository.TestimonialRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Public paged reads plus admin CRUD for testimonials. */
@Service
@Transactional(readOnly = true)
public class TestimonialService {

    private static final int DEFAULT_PAGE_SIZE = 6;

    private final TestimonialRepository testimonialRepository;
    private final TestimonialMapper testimonialMapper;

    public TestimonialService(TestimonialRepository testimonialRepository, TestimonialMapper testimonialMapper) {
        this.testimonialRepository = testimonialRepository;
        this.testimonialMapper = testimonialMapper;
    }

    /**
     * Paged, filterable list of published testimonials (newest first). Mirrors
     * the frontend mock: 1-based page, default page size 6, free-text {@code q}
     * over author/content/location/country, optional exact {@code rating}.
     */
    public PageResponse<TestimonialResponse> list(String q, Integer rating, Integer page, Integer pageSize) {
        int pageNumber = page == null ? 1 : Math.max(1, page);
        int size = pageSize == null ? DEFAULT_PAGE_SIZE : Math.max(1, pageSize);
        String normalizedQ = q == null ? "" : q.trim().toLowerCase();

        Page<Testimonial> result = testimonialRepository.search(
                normalizedQ,
                rating,
                PageRequest.of(pageNumber - 1, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        return new PageResponse<>(
                result.getContent().stream().map(testimonialMapper::toResponse).toList(),
                result.getTotalElements(),
                pageNumber,
                size);
    }

    /** Admin: every testimonial (incl. unpublished), newest first. */
    public List<TestimonialResponse> adminList() {
        return testimonialRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(testimonialMapper::toResponse)
                .toList();
    }

    @Transactional
    public TestimonialResponse create(TestimonialRequest request) {
        Testimonial testimonial = new Testimonial();
        testimonial.setId("tst-" + UUID.randomUUID());
        testimonial.setCreatedAt(Instant.now());
        testimonialMapper.applyRequest(request, testimonial);
        return testimonialMapper.toResponse(testimonialRepository.save(testimonial));
    }

    @Transactional
    public TestimonialResponse update(String id, TestimonialRequest request) {
        Testimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Testimonial", id));
        testimonialMapper.applyRequest(request, testimonial);
        return testimonialMapper.toResponse(testimonialRepository.save(testimonial));
    }

    @Transactional
    public void delete(String id) {
        Testimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Testimonial", id));
        testimonialRepository.delete(testimonial);
    }

    /** Flips the published flag and returns the updated testimonial. */
    @Transactional
    public TestimonialResponse togglePublish(String id) {
        Testimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Testimonial", id));
        testimonial.setPublished(!testimonial.isPublished());
        return testimonialMapper.toResponse(testimonialRepository.save(testimonial));
    }
}
