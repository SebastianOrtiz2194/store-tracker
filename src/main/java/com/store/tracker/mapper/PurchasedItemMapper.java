package com.store.tracker.mapper;

import com.store.tracker.dto.PurchasedItemDto;
import com.store.tracker.entity.PurchasedItem;
import com.store.tracker.entity.Visit;

import java.util.List;

/**
 * Conversion helpers between {@link PurchasedItem} entities and {@link PurchasedItemDto}
 * transport objects. All methods are null-safe: a {@code null} input returns {@code null}
 * for single conversions and an empty list for the bulk variant.
 */
public class PurchasedItemMapper {

    private PurchasedItemMapper() {
    }

    /**
     * Converts a {@link PurchasedItem} entity to its DTO representation.
     *
     * @param entity the entity to convert; may be {@code null}
     * @return the DTO, or {@code null} when the entity is {@code null}
     */
    public static PurchasedItemDto toDto(PurchasedItem entity) {
        if (entity == null) return null;
        return new PurchasedItemDto(entity.getId(), entity.getName(), entity.getPrice(), entity.getQuantity());
    }

    /**
     * Converts a list of entities to their DTO representations.
     *
     * @param items the entities to convert; may be {@code null}
     * @return a list of DTOs, or an empty list when the input is {@code null}
     */
    public static List<PurchasedItemDto> toDtoList(List<PurchasedItem> items) {
        if (items == null) return List.of();
        return items.stream()
                .map(PurchasedItemMapper::toDto)
                .toList();
    }

    /**
     * Converts a {@link PurchasedItemDto} to a new entity linked to the given visit.
     *
     * @param dto   the DTO to convert; may be {@code null}
     * @param visit the visit the resulting entity will belong to
     * @return the new entity, or {@code null} when the DTO is {@code null}
     */
    public static PurchasedItem toEntity(PurchasedItemDto dto, Visit visit) {
        if (dto == null) return null;
        return new PurchasedItem(dto.name(), dto.price(), dto.quantity(), visit);
    }
}
