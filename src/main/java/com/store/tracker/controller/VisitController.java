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
 * Controlador REST para la gestión de visitas.
 * Ahora es una capa delegadora delgada ("thin controller"), 
 * cumpliendo con el estándar de arquitectura por capas.
 */
@RestController
@RequestMapping("/api/visits")
@Tag(name = "Visitas", description = "Endpoints para la gestión de entradas, salidas y compras de clientes")
public class VisitController {

    @Autowired
    private VisitService visitService;

    // 1. Registrar una persona que entra al establecimiento
    @PostMapping("/enter")
    @Operation(summary = "Registrar entrada", description = "Crea un nuevo registro de visita indicando que una persona ha entrado al local")
    public ResponseEntity<ApiResponse<VisitResponse>> registerEntry(@Valid @RequestBody VisitEntryRequest request) {
        VisitResponse response = visitService.registerEntry(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Entrada registrada con éxito"));
    }

    // 2. Registrar salida de la persona y qué compró
    @PutMapping("/{id}/leave")
    @Operation(summary = "Registrar salida y compras", description = "Actualiza una visita existente con la hora de salida y la lista de artículos comprados")
    public ResponseEntity<ApiResponse<VisitResponse>> registerExit(
            @Parameter(description = "ID único de la visita") @PathVariable Long id, 
            @Valid @RequestBody VisitLeaveRequest request) {
        
        VisitResponse response = visitService.registerExit(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Salida registrada con éxito"));
    }

    // 3. Ver todas las visitas registradas (historial completo)
    @GetMapping
    @Operation(summary = "Obtener historial completo", description = "Retorna una lista de todas las visitas registradas en el sistema, históricas y activas")
    public ResponseEntity<ApiResponse<List<VisitResponse>>> getAllVisits() {
        List<VisitResponse> visits = visitService.getAllVisits();
        return ResponseEntity.ok(ApiResponse.success(visits, "Lista de visitas obtenida"));
    }

    // 4. Ver qué personas están actualmente dentro del establecimiento (sin hora de salida)
    @GetMapping("/active")
    @Operation(summary = "Listar visitas activas", description = "Retorna solo las personas que han entrado pero aún no han registrado su salida")
    public ResponseEntity<ApiResponse<List<VisitResponse>>> getActiveVisits() {
        List<VisitResponse> visits = visitService.getActiveVisits();
        return ResponseEntity.ok(ApiResponse.success(visits, "Lista de visitas activas obtenida"));
    }
}
