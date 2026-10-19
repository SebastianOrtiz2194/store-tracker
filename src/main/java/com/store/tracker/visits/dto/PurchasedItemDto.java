package com.store.tracker.visits.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Data transfer object representing a single item purchased during a visit.
 * Used in request payloads for registering exits and in responses that include
 * the list of items associated with a visit.
 *
 * @param id       the database identifier; {@code null} for new items not yet persisted
 * @param name     the item's display name; must not be blank and at most 120 characters
 * @param price    the unit price; must be positive with at most two decimals
 * @param quantity the number of units purchased; between {@code 1} and {@code 1000}
 */
public record PurchasedItemDto(
    Long id,
    @NotBlank(message = "Item name is required")
    @Size(max = 120, message = "Item name must be at most 120 characters")
    String name,
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Price must have at most 2 decimals")
    BigDecimal price,
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Minimum quantity is 1")
    @Max(value = 1000, message = "Maximum quantity is 1000")
    Integer quantity
) {}
