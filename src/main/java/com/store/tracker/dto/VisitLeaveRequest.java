package com.store.tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * DTO para la solicitud de salida y registro de compras.
 */
public class VisitLeaveRequest {
    @NotBlank(message = "Los artículos comprados no pueden estar vacíos")
    private String purchasedItems;

    @NotNull(message = "El monto total gastado es requerido")
    @PositiveOrZero(message = "El monto total debe ser cero o positivo")
    private Double totalSpent;

    public VisitLeaveRequest() {}

    public String getPurchasedItems() { return purchasedItems; }
    public void setPurchasedItems(String purchasedItems) { this.purchasedItems = purchasedItems; }

    public Double getTotalSpent() { return totalSpent; }
    public void setTotalSpent(Double totalSpent) { this.totalSpent = totalSpent; }
}
