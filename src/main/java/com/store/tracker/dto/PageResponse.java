package com.store.tracker.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Transport representation of a page of results.
 *
 * <p>Exposes a stable, framework-independent shape instead of serialising
 * Spring Data's {@link Page} directly, whose JSON form is an implementation
 * detail and changes between versions.
 *
 * @param items         the page content, never {@code null}
 * @param page          the zero-based page index
 * @param size          the requested page size
 * @param totalElements the total number of matching elements
 * @param totalPages    the total number of pages
 * @param hasNext       whether another page follows
 * @param <T>           the element type
 */
public record PageResponse<T>(
    List<T> items,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean hasNext
) {
    /**
     * Adapts a Spring Data page to the transport shape.
     *
     * @param page the source page; must not be {@code null}
     * @param <T>  the element type
     * @return the equivalent {@link PageResponse}
     */
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.hasNext());
    }
}
