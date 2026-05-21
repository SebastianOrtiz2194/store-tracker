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

        VisitResponse response = new VisitResponse();
        response.setId(entity.getId());
        response.setPersonName(entity.getPersonName());
        response.setEntryTime(entity.getEntryTime());
        response.setExitTime(entity.getExitTime());
        response.setPurchasedItems(toItemDtoList(entity.getPurchasedItems()));
        response.setTotalSpent(entity.getTotalSpent());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());

        return response;
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
        PurchasedItemDto dto = new PurchasedItemDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPrice(entity.getPrice());
        dto.setQuantity(entity.getQuantity());
        return dto;
    }

    /**
     * Maps a PurchasedItemDto to a PurchasedItem entity.
     */
    public static PurchasedItem toItemEntity(PurchasedItemDto dto, Visit visit) {
        if (dto == null) return null;
        PurchasedItem entity = new PurchasedItem(dto.getName(), dto.getPrice(), dto.getQuantity(), visit);
        return entity;
    }
}
