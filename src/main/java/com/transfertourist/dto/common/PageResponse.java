package com.transfertourist.dto.common;

import java.util.List;

/**
 * Standard paginated response envelope. Mirrors the frontend {@code Page<T>}
 * type ({@code items}, {@code total}, 1-based {@code page}, {@code pageSize}).
 */
public record PageResponse<T>(
        List<T> items,
        long total,
        int page,
        int pageSize
) {
}
