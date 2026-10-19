package com.store.tracker.visits.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response payload returned for any visit-related endpoint.
 * Contains the visit's full state including entry/exit timestamps, the list of
 * purchased items, and the total amount spent.
 *
 * @param id             the visit's database identifier
 * @param personName     the visitor's name
 * @param entryTime      the time the visitor entered the store
 * @param exitTime       the time the visitor left the store; {@code null} if still inside
 * @param purchasedItems the items bought during the visit; empty while the visitor is inside
 * @param totalSpent     the derived amount spent; {@code null} until the visit has an exit
 */
public record VisitResponse(
    Long id,
    String personName,
    LocalDateTime entryTime,
    LocalDateTime exitTime,
    List<PurchasedItemDto> purchasedItems,
    BigDecimal totalSpent
) {
    /**
     * Convenience constructor for entry-only responses, where exit and purchases
     * are not yet known.
     *
     * @param id         the visit's database identifier
     * @param personName the visitor's name
     */
    public VisitResponse(Long id, String personName) {
        this(id, personName, null, null, null, null);
    }
}
