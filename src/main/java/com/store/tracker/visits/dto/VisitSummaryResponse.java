package com.store.tracker.visits.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Lightweight representation of a visit used by the paginated history endpoint.
 *
 * <p>It deliberately omits the purchased items: the list view does not need
 * them, and not loading them is what keeps the query count constant regardless
 * of page size. Clients that need the items fetch the visit by id.
 *
 * @param id         the visit's database identifier
 * @param personName the visitor's name
 * @param entryTime  the time the visitor entered the store
 * @param exitTime   the time the visitor left the store; {@code null} if still inside
 * @param totalSpent the derived amount spent; {@code null} until the visit has an exit
 */
public record VisitSummaryResponse(
    Long id,
    String personName,
    LocalDateTime entryTime,
    LocalDateTime exitTime,
    BigDecimal totalSpent
) {}
