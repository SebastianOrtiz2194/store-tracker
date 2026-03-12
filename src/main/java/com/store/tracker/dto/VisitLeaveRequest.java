package com.store.tracker.dto;

/**
 * DTO para la solicitud de salida y registro de compras.
 */
public class VisitLeaveRequest {
    private String purchasedItems;
    private Double totalSpent;

    public VisitLeaveRequest() {}

    public String getPurchasedItems() { return purchasedItems; }
    public void setPurchasedItems(String purchasedItems) { this.purchasedItems = purchasedItems; }

    public Double getTotalSpent() { return totalSpent; }
    public void setTotalSpent(Double totalSpent) { this.totalSpent = totalSpent; }
}
