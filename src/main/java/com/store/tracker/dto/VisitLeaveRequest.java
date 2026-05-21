package com.store.tracker.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * Request body for recording a visitor's exit and purchased items.
 */
public class VisitLeaveRequest {
    
    @NotNull(message = "Purchased items list is required (can be empty)")
    @Valid
    private List<PurchasedItemDto> purchasedItems;

    @NotNull(message = "Total spent amount is required")
    @PositiveOrZero(message = "Total spent must be zero or positive")
    private Double totalSpent;

    public VisitLeaveRequest() {}

    public List<PurchasedItemDto> getPurchasedItems() { return purchasedItems; }
    public void setPurchasedItems(List<PurchasedItemDto> purchasedItems) { this.purchasedItems = purchasedItems; }

    public Double getTotalSpent() { return totalSpent; }
    public void setTotalSpent(Double totalSpent) { this.totalSpent = totalSpent; }
}
