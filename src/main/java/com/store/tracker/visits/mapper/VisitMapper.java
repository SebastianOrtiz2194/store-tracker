package com.store.tracker.visits.mapper;

import com.store.tracker.visits.dto.VisitResponse;
import com.store.tracker.visits.entity.Visit;

/**
 * Conversion helpers between {@link Visit} entities and {@link VisitResponse}
 * transport objects.
 */
public class VisitMapper {

    private VisitMapper() {
    }

    /**
     * Converts a {@link Visit} entity to its DTO representation, including the
     * associated purchased items mapped via {@link PurchasedItemMapper}.
     *
     * @param entity the entity to convert; may be {@code null}
     * @return the response DTO, or {@code null} when the entity is {@code null}
     */
    public static VisitResponse toResponse(Visit entity) {
        if (entity == null) return null;

        return new VisitResponse(
            entity.getId(),
            entity.getPersonName(),
            entity.getEntryTime(),
            entity.getExitTime(),
            PurchasedItemMapper.toDtoList(entity.getPurchasedItems()),
            entity.getTotalSpent()
        );
    }
}
