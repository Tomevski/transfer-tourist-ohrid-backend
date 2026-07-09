package com.transfertourist.util;

import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Derives a URL-friendly slug from a location name, byte-for-byte identical to
 * the frontend mock's {@code slugify}: lower-case, trim, collapse every run of
 * non-alphanumerics to a single {@code -}, then strip a leading/trailing {@code -}.
 */
@Component
public class Slugifier {

    public String slugify(String name) {
        return name.toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }
}
