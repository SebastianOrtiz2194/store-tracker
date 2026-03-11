package com.store.tracker.controller;

import com.store.tracker.model.Visit;
import com.store.tracker.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/visits")
public class VisitController {

    @Autowired
    private VisitRepository visitRepository;

    // 1. Registrar una persona que entra al establecimiento
    @PostMapping("/enter")
    public Visit registerEntry(@RequestBody Visit request) {
        Visit visit = new Visit(request.getPersonName(), LocalDateTime.now());
        return visitRepository.save(visit);
    }

    // 2. Registrar salida de la persona y qué compró
    @PutMapping("/{id}/leave")
    public ResponseEntity<Visit> registerExit(@PathVariable Long id, @RequestBody Visit request) {
        Optional<Visit> optionalVisit = visitRepository.findById(id);
        
        if (optionalVisit.isPresent()) {
            Visit visit = optionalVisit.get();
            visit.setExitTime(LocalDateTime.now());
            visit.setPurchasedItems(request.getPurchasedItems());
            visit.setTotalSpent(request.getTotalSpent());
            return ResponseEntity.ok(visitRepository.save(visit));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 3. Ver todas las visitas registradas (historial completo)
    @GetMapping
    public List<Visit> getAllVisits() {
        return visitRepository.findAll();
    }

    // 4. Ver qué personas están actualmente dentro del establecimiento (sin hora de salida)
    @GetMapping("/active")
    public List<Visit> getActiveVisits() {
        return visitRepository.findByExitTimeIsNull();
    }
}
