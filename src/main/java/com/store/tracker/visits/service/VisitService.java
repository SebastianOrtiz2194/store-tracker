package com.store.tracker.visits.service;

import com.store.tracker.dto.PageResponse;
import com.store.tracker.visits.dto.VisitEntryRequest;
import com.store.tracker.visits.dto.VisitLeaveRequest;
import com.store.tracker.visits.dto.VisitResponse;
import com.store.tracker.visits.dto.VisitSummaryResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Defines business operations for visit management.
 */
public interface VisitService {

    /**
     * Registers a visitor's entry.
     *
     * @param request the entry request
     * @return the created visit
     */
    VisitResponse registerEntry(VisitEntryRequest request);

    /**
     * Closes an open visit and derives its total from the purchased items.
     *
     * @param id      the visit identifier
     * @param request the leave request
     * @return the updated visit
     */
    VisitResponse registerExit(Long id, VisitLeaveRequest request);

    /**
     * Returns a single visit with its purchased items.
     *
     * @param id the visit identifier
     * @return the visit
     */
    VisitResponse getVisitById(Long id);

    /**
     * Returns a page of the visit history, optionally filtered by entry time.
     *
     * @param from     the inclusive lower bound, or {@code null}
     * @param to       the inclusive upper bound, or {@code null}
     * @param pageable the requested page and sort
     * @return a page of visit summaries
     */
    PageResponse<VisitSummaryResponse> getAllVisits(LocalDateTime from, LocalDateTime to, Pageable pageable);

    /**
     * Returns the visits that are still open.
     *
     * @return the active visits
     */
    List<VisitResponse> getActiveVisits();
}
