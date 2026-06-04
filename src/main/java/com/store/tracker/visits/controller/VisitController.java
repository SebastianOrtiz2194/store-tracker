package com.store.tracker.visits.controller;

import com.store.tracker.dto.ResponseEnvelope;
import com.store.tracker.visits.dto.VisitEntryRequest;
import com.store.tracker.visits.dto.VisitLeaveRequest;
import com.store.tracker.visits.dto.VisitResponse;
import com.store.tracker.visits.service.VisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    private final VisitService visitService;

    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }

    @PostMapping("/enter")
    @Operation(
        operationId = "registerEntry",
        summary = "Register entry",
        description = "Creates a new visit record when a person enters the store"
    )
    @ApiResponse(responseCode = "200", description = "Entry registered successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request payload")
    public ResponseEntity<ResponseEnvelope<VisitResponse>> registerEntry(@Valid @RequestBody VisitEntryRequest request) {
        VisitResponse response = visitService.registerEntry(request);
        return ResponseEntity.ok(ResponseEnvelope.success(response, "Entry registered successfully"));
    }

    @PutMapping("/{id}/leave")
    @Operation(
        operationId = "registerExit",
        summary = "Register exit and purchases",
        description = "Updates an existing visit with exit time and purchased items"
    )
    @ApiResponse(responseCode = "200", description = "Exit registered successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request payload")
    @ApiResponse(responseCode = "404", description = "Visit not found")
    public ResponseEntity<ResponseEnvelope<VisitResponse>> registerExit(
            @Parameter(description = "Unique visit ID") @PathVariable Long id,
            @Valid @RequestBody VisitLeaveRequest request) {

        VisitResponse response = visitService.registerExit(id, request);
        return ResponseEntity.ok(ResponseEnvelope.success(response, "Exit registered successfully"));
    }

    @GetMapping("/{id}")
    @Operation(
        operationId = "getVisitById",
        summary = "Get visit by ID",
        description = "Returns a single visit identified by its unique ID"
    )
    @ApiResponse(responseCode = "200", description = "Visit found")
    @ApiResponse(responseCode = "404", description = "Visit not found")
    public ResponseEntity<ResponseEnvelope<VisitResponse>> getVisitById(
            @Parameter(description = "Unique visit ID") @PathVariable Long id) {
        VisitResponse visit = visitService.getVisitById(id);
        return ResponseEntity.ok(ResponseEnvelope.success(visit, "Visit retrieved successfully"));
    }

    @GetMapping
    @Operation(
        operationId = "getAllVisits",
        summary = "Get full history",
        description = "Returns all visits recorded in the system, both active and completed"
    )
    @ApiResponse(responseCode = "200", description = "Visit list retrieved")
    public ResponseEntity<ResponseEnvelope<List<VisitResponse>>> getAllVisits() {
        List<VisitResponse> visits = visitService.getAllVisits();
        return ResponseEntity.ok(ResponseEnvelope.success(visits, "Visit list retrieved"));
    }

    @GetMapping("/active")
    @Operation(
        operationId = "getActiveVisits",
        summary = "List active visits",
        description = "Returns visitors who have entered but not yet recorded their exit"
    )
    @ApiResponse(responseCode = "200", description = "Active visit list retrieved")
    public ResponseEntity<ResponseEnvelope<List<VisitResponse>>> getActiveVisits() {
        List<VisitResponse> visits = visitService.getActiveVisits();
        return ResponseEntity.ok(ResponseEnvelope.success(visits, "Active visit list retrieved"));
    }
}
