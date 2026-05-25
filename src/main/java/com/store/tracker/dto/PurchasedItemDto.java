package com.store.tracker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PurchasedItemDto(
    Long id,
    @NotBlank(message = "Item name is required") String name,
    @NotNull(message = "Price is required") @Positive(message = "Price must be greater than zero") Double price,
    @NotNull(message = "Quantity is required") @Min(value = 1, message = "Minimum quantity is 1") Integer quantity
) {}
