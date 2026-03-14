package com.store.tracker.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para representar una visita en las respuestas de la API.
 */
public class VisitResponse {
    private Long id;
    private String personName;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private List<PurchasedItemDto> purchasedItems;
    private Double totalSpent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public VisitResponse() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPersonName() { return personName; }
    public void setPersonName(String personName) { this.personName = personName; }

    public LocalDateTime getEntryTime() { return entryTime; }
    public void setEntryTime(LocalDateTime entryTime) { this.entryTime = entryTime; }

    public LocalDateTime getExitTime() { return exitTime; }
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }

    public List<PurchasedItemDto> getPurchasedItems() { return purchasedItems; }
    public void setPurchasedItems(List<PurchasedItemDto> purchasedItems) { this.purchasedItems = purchasedItems; }

    public Double getTotalSpent() { return totalSpent; }
    public void setTotalSpent(Double totalSpent) { this.totalSpent = totalSpent; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
