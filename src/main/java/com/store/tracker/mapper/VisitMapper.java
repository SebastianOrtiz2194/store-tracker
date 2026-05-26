package com.store.tracker.mapper;

import com.store.tracker.dto.PurchasedItemDto;
import com.store.tracker.dto.VisitResponse;
import com.store.tracker.entity.PurchasedItem;
import com.store.tracker.entity.Visit;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Converts between Visit entities and response DTOs.
 */
public class VisitMapper {

    /**
     * Maps a Visit entity to a VisitResponse DTO.
     */
    public static VisitResponse toResponse(Visit entity) {
        if (entity == null) return null;

        return new VisitResponse(
            entity.getId(),
            entity.getPersonName(),
            entity.getEntryTime(),
            entity.getExitTime(),
            toItemDtoList(entity.getPurchasedItems()),
            entity.getTotalSpent(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    /**
     * Maps a list of PurchasedItem entities to DTOs.
     */
    public static List<PurchasedItemDto> toItemDtoList(List<PurchasedItem> items) {
        if (items == null) return Collections.emptyList();
        return items.stream()
                .map(VisitMapper::toItemDto)
                .collect(Collectors.toList());
    }

    /**
     * Maps a PurchasedItem entity to a DTO.
     */
    public static PurchasedItemDto toItemDto(PurchasedItem entity) {
        if (entity == null) return null;
        return new PurchasedItemDto(entity.getId(), entity.getName(), entity.getPrice(), entity.getQuantity());
    }

    /**
     * Maps a PurchasedItemDto to a PurchasedItem entity.
     */
    public static PurchasedItem toItemEntity(PurchasedItemDto dto, Visit visit) {
        if (dto == null) return null;
        return new PurchasedItem(dto.name(), dto.price(), dto.quantity(), visit);
    }
}
