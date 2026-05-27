package com.store.tracker.mapper;

import com.store.tracker.dto.PurchasedItemDto;
import com.store.tracker.entity.PurchasedItem;
import com.store.tracker.entity.Visit;

import java.util.List;

public class PurchasedItemMapper {

    public static PurchasedItemDto toDto(PurchasedItem entity) {
        if (entity == null) return null;
        return new PurchasedItemDto(entity.getId(), entity.getName(), entity.getPrice(), entity.getQuantity());
    }

    public static List<PurchasedItemDto> toDtoList(List<PurchasedItem> items) {
        if (items == null) return List.of();
        return items.stream()
                .map(PurchasedItemMapper::toDto)
                .toList();
    }

    public static PurchasedItem toEntity(PurchasedItemDto dto, Visit visit) {
        if (dto == null) return null;
        return new PurchasedItem(dto.name(), dto.price(), dto.quantity(), visit);
    }
}
