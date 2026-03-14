package com.store.tracker.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * DTO para la solicitud de salida y registro de compras.
 * Ahora incluye una lista de artículos normalizada.
 */
public class VisitLeaveRequest {
    
    @NotEmpty(message = "Debe registrar al menos un artículo comprado")
    @Valid
    private List<PurchasedItemDto> purchasedItems;

    @NotNull(message = "El monto total gastado es requerido")
    @PositiveOrZero(message = "El monto total debe ser cero o positivo")
    private Double totalSpent;

    public VisitLeaveRequest() {}

    public List<PurchasedItemDto> getPurchasedItems() { return purchasedItems; }
    public void setPurchasedItems(List<PurchasedItemDto> purchasedItems) { this.purchasedItems = purchasedItems; }

    public Double getTotalSpent() { return totalSpent; }
    public void setTotalSpent(Double totalSpent) { this.totalSpent = totalSpent; }
}
