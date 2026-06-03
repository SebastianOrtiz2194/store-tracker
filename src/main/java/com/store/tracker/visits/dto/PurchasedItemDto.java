package com.store.tracker.visits.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Data transfer object representing a single item purchased during a visit.
 * Used in request payloads for registering exits and in responses that include
 * the list of items associated with a visit.
 *
 * @param id       the database identifier; {@code null} for new items not yet persisted
 * @param name     the item's display name; must not be blank
 * @param price    the unit price; must be positive
 * @param quantity the number of units purchased; must be at least {@code 1}
 */
public record PurchasedItemDto(
    Long id,
    @NotBlank(message = "Item name is required") String name,
    @NotNull(message = "Price is required") @Positive(message = "Price must be greater than zero") Double price,
    @NotNull(message = "Quantity is required") @Min(value = 1, message = "Minimum quantity is 1") Integer quantity
) {}
