package com.store.tracker.visits.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Request payload for registering a customer's exit and their associated purchases.
 *
 * <p>The amount spent is deliberately absent: it is derived on the server from the
 * line items so a client can never declare its own revenue.
 *
 * @param purchasedItems the items bought during the visit; required but may be empty,
 *                       at most 100 lines
 */
public record VisitLeaveRequest(
    @NotNull(message = "Purchased items list is required (can be empty)")
    @Size(max = 100, message = "At most 100 purchased items are allowed per visit")
    @Valid
    List<PurchasedItemDto> purchasedItems
) {}
