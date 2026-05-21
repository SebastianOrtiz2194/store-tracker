package com.store.tracker.controller;

import com.store.tracker.dto.ApiResponse;
import com.store.tracker.dto.VisitEntryRequest;
import com.store.tracker.dto.VisitLeaveRequest;
import com.store.tracker.dto.VisitResponse;
import com.store.tracker.service.VisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for visit management.
 * Delegates all business logic to VisitService.
 */
@RestController
@RequestMapping("/api/visits")
@Tag(name = "Visits", description = "Endpoints for managing customer entries, exits, and purchases")
public class VisitController {

    @Autowired
    private VisitService visitService;

    @PostMapping("/enter")
    @Operation(summary = "Register entry", description = "Creates a new visit record when a person enters the store")
    public ResponseEntity<ApiResponse<VisitResponse>> registerEntry(@Valid @RequestBody VisitEntryRequest request) {
        VisitResponse response = visitService.registerEntry(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Entry registered successfully"));
    }

    @PutMapping("/{id}/leave")
    @Operation(summary = "Register exit and purchases", description = "Updates an existing visit with exit time and purchased items")
    public ResponseEntity<ApiResponse<VisitResponse>> registerExit(
            @Parameter(description = "Unique visit ID") @PathVariable Long id, 
            @Valid @RequestBody VisitLeaveRequest request) {
        
        VisitResponse response = visitService.registerExit(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Exit registered successfully"));
    }

    @GetMapping
    @Operation(summary = "Get full history", description = "Returns all visits recorded in the system, both active and completed")
    public ResponseEntity<ApiResponse<List<VisitResponse>>> getAllVisits() {
        List<VisitResponse> visits = visitService.getAllVisits();
        return ResponseEntity.ok(ApiResponse.success(visits, "Visit list retrieved"));
    }

    @GetMapping("/active")
    @Operation(summary = "List active visits", description = "Returns visitors who have entered but not yet recorded their exit")
    public ResponseEntity<ApiResponse<List<VisitResponse>>> getActiveVisits() {
        List<VisitResponse> visits = visitService.getActiveVisits();
        return ResponseEntity.ok(ApiResponse.success(visits, "Active visit list retrieved"));
    }
}
