package com.store.tracker.visits.service;

import com.store.tracker.visits.dto.VisitEntryRequest;
import com.store.tracker.visits.dto.VisitLeaveRequest;
import com.store.tracker.visits.dto.VisitResponse;

import java.util.List;

/**
 * Defines business operations for visit management.
 */
public interface VisitService {
    VisitResponse registerEntry(VisitEntryRequest request);
    VisitResponse registerExit(Long id, VisitLeaveRequest request);
    VisitResponse getVisitById(Long id);
    List<VisitResponse> getAllVisits();
    List<VisitResponse> getActiveVisits();
}
