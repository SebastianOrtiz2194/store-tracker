package com.store.tracker.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * Request payload for registering a customer's exit and their associated purchases.
 *
 * @param purchasedItems the list of items bought during the visit; required but may be empty
 * @param totalSpent     the total amount spent; required and must be zero or positive
 */
public record VisitLeaveRequest(
    @NotNull(message = "Purchased items list is required (can be empty)") @Valid List<PurchasedItemDto> purchasedItems,
    @NotNull(message = "Total spent amount is required") @PositiveOrZero(message = "Total spent must be zero or positive") Double totalSpent
) {}
