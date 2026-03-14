package com.store.tracker.mapper;

import com.store.tracker.dto.PurchasedItemDto;
import com.store.tracker.dto.VisitResponse;
import com.store.tracker.entity.PurchasedItem;
import com.store.tracker.entity.Visit;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre entidades y DTOs.
 */
public class VisitMapper {

    /**
     * Convierte una entidad Visit a un DTO VisitResponse.
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
     * Convierte una lista de PurchasedItem a PurchasedItemDto.
     */
    public static List<PurchasedItemDto> toItemDtoList(List<PurchasedItem> items) {
        if (items == null) return Collections.emptyList();
        return items.stream()
                .map(VisitMapper::toItemDto)
                .collect(Collectors.toList());
    }

    /**
     * Convierte un PurchasedItem a PurchasedItemDto.
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
     * Convierte un PurchasedItemDto a PurchasedItem.
     */
    public static PurchasedItem toItemEntity(PurchasedItemDto dto, Visit visit) {
        if (dto == null) return null;
        PurchasedItem entity = new PurchasedItem();
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setQuantity(dto.getQuantity());
        entity.setVisit(visit);
        return entity;
    }
}
