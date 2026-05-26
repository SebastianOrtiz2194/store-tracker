package com.store.tracker.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

public record VisitLeaveRequest(
    @NotNull(message = "Purchased items list is required (can be empty)") @Valid List<PurchasedItemDto> purchasedItems,
    @NotNull(message = "Total spent amount is required") @PositiveOrZero(message = "Total spent must be zero or positive") Double totalSpent
) {}
