package com.store.tracker.visits.mapper;

import com.store.tracker.visits.dto.VisitSummaryResponse;
import com.store.tracker.visits.entity.Visit;

/**
 * Conversion helpers between {@link Visit} entities and the summary transport
 * object used by list endpoints.
 */
public class VisitSummaryMapper {

    private VisitSummaryMapper() {
    }

    /**
     * Converts a visit to its summary representation without touching the
     * lazily loaded purchased items.
     *
     * @param entity the entity to convert; may be {@code null}
     * @return the summary DTO, or {@code null} when the entity is {@code null}
     */
    public static VisitSummaryResponse toResponse(Visit entity) {
        if (entity == null) return null;
        return new VisitSummaryResponse(
                entity.getId(),
                entity.getPersonName(),
                entity.getEntryTime(),
                entity.getExitTime(),
                entity.getTotalSpent());
    }
}
