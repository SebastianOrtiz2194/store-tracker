package com.store.tracker.mapper;

import com.store.tracker.dto.VisitResponse;
import com.store.tracker.entity.Visit;

public class VisitMapper {

    public static VisitResponse toResponse(Visit entity) {
        if (entity == null) return null;

        return new VisitResponse(
            entity.getId(),
            entity.getPersonName(),
            entity.getEntryTime(),
            entity.getExitTime(),
            PurchasedItemMapper.toDtoList(entity.getPurchasedItems()),
            entity.getTotalSpent(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
