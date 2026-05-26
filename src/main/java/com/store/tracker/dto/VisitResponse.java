package com.store.tracker.dto;

import java.time.LocalDateTime;
import java.util.List;

public record VisitResponse(
    Long id,
    String personName,
    LocalDateTime entryTime,
    LocalDateTime exitTime,
    List<PurchasedItemDto> purchasedItems,
    Double totalSpent,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public VisitResponse(Long id, String personName) {
        this(id, personName, null, null, null, null, null, null);
    }
}
