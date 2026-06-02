package com.store.tracker.controller;

import com.store.tracker.dto.ApiResponse;
import com.store.tracker.dto.VisitEntryRequest;
import com.store.tracker.dto.VisitLeaveRequest;
import com.store.tracker.dto.VisitResponse;
import com.store.tracker.service.VisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Entry registered successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload")
    })
    public ResponseEntity<ApiResponse<VisitResponse>> registerEntry(@Valid @RequestBody VisitEntryRequest request) {
        VisitResponse response = visitService.registerEntry(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Entry registered successfully"));
    }

    @PutMapping("/{id}/leave")
    @Operation(
        operationId = "registerExit",
        summary = "Register exit and purchases",
        description = "Updates an existing visit with exit time and purchased items"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Exit registered successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Visit not found")
    })
    public ResponseEntity<ApiResponse<VisitResponse>> registerExit(
            @Parameter(description = "Unique visit ID") @PathVariable Long id,
            @Valid @RequestBody VisitLeaveRequest request) {

        VisitResponse response = visitService.registerExit(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Exit registered successfully"));
    }

    @GetMapping
    @Operation(
        operationId = "getAllVisits",
        summary = "Get full history",
        description = "Returns all visits recorded in the system, both active and completed"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Visit list retrieved")
    public ResponseEntity<ApiResponse<List<VisitResponse>>> getAllVisits() {
        List<VisitResponse> visits = visitService.getAllVisits();
        return ResponseEntity.ok(ApiResponse.success(visits, "Visit list retrieved"));
    }

    @GetMapping("/active")
    @Operation(
        operationId = "getActiveVisits",
        summary = "List active visits",
        description = "Returns visitors who have entered but not yet recorded their exit"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Active visit list retrieved")
    public ResponseEntity<ApiResponse<List<VisitResponse>>> getActiveVisits() {
        List<VisitResponse> visits = visitService.getActiveVisits();
        return ResponseEntity.ok(ApiResponse.success(visits, "Active visit list retrieved"));
    }
}
