package com.store.tracker.controller;

import com.store.tracker.dto.ApiResponse;
import com.store.tracker.dto.VisitEntryRequest;
import com.store.tracker.dto.VisitLeaveRequest;
import com.store.tracker.dto.VisitResponse;
import com.store.tracker.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de visitas.
 * Ahora es una capa delegadora delgada ("thin controller"), 
 * cumpliendo con el estándar de arquitectura por capas.
 */
@RestController
@RequestMapping("/api/visits")
public class VisitController {

    @Autowired
    private VisitService visitService;

    // 1. Registrar una persona que entra al establecimiento
    @PostMapping("/enter")
    public ResponseEntity<ApiResponse<VisitResponse>> registerEntry(@RequestBody VisitEntryRequest request) {
        VisitResponse response = visitService.registerEntry(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Entrada registrada con éxito"));
    }

    // 2. Registrar salida de la persona y qué compró
    @PutMapping("/{id}/leave")
    public ResponseEntity<ApiResponse<VisitResponse>> registerExit(
            @PathVariable Long id, 
            @RequestBody VisitLeaveRequest request) {
        
        VisitResponse response = visitService.registerExit(id, request);
        
        if (response != null) {
            return ResponseEntity.ok(ApiResponse.success(response, "Salida registrada con éxito"));
        } else {
            return ResponseEntity.status(404).body(ApiResponse.error("Visita no encontrada con ID: " + id));
        }
    }

    // 3. Ver todas las visitas registradas (historial completo)
    @GetMapping
    public ResponseEntity<ApiResponse<List<VisitResponse>>> getAllVisits() {
        List<VisitResponse> visits = visitService.getAllVisits();
        return ResponseEntity.ok(ApiResponse.success(visits, "Lista de visitas obtenida"));
    }

    // 4. Ver qué personas están actualmente dentro del establecimiento (sin hora de salida)
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<VisitResponse>>> getActiveVisits() {
        List<VisitResponse> visits = visitService.getActiveVisits();
        return ResponseEntity.ok(ApiResponse.success(visits, "Lista de visitas activas obtenida"));
    }
}
