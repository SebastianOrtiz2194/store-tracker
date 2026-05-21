package com.store.tracker.service;

import com.store.tracker.dto.VisitEntryRequest;
import com.store.tracker.dto.VisitLeaveRequest;
import com.store.tracker.dto.VisitResponse;

import java.util.List;

/**
 * Defines business operations for visit management.
 */
public interface VisitService {
    VisitResponse registerEntry(VisitEntryRequest request);
    VisitResponse registerExit(Long id, VisitLeaveRequest request);
    List<VisitResponse> getAllVisits();
    List<VisitResponse> getActiveVisits();
}
