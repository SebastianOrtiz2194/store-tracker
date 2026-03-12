package com.store.tracker.service.impl;

import com.store.tracker.dto.VisitEntryRequest;
import com.store.tracker.dto.VisitLeaveRequest;
import com.store.tracker.dto.VisitResponse;
import com.store.tracker.entity.Visit;
import com.store.tracker.mapper.VisitMapper;
import com.store.tracker.repository.VisitRepository;
import com.store.tracker.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de la capa de servicio para Visit.
 * Aquí reside la lógica de negocio, fuera del controlador.
 */
@Service
public class VisitServiceImpl implements VisitService {

    @Autowired
    private VisitRepository visitRepository;

    @Override
    public VisitResponse registerEntry(VisitEntryRequest request) {
        Visit visit = new Visit(request.getPersonName(), LocalDateTime.now());
        Visit savedVisit = visitRepository.save(visit);
        return VisitMapper.toResponse(savedVisit);
    }

    @Override
    public VisitResponse registerExit(Long id, VisitLeaveRequest request) {
        // En esta fase, si no existe devolvemos null. 
        // En la Fase 2 implementaremos excepciones personalizadas.
        return visitRepository.findById(id).map(visit -> {
            visit.setExitTime(LocalDateTime.now());
            visit.setPurchasedItems(request.getPurchasedItems());
            visit.setTotalSpent(request.getTotalSpent());
            Visit updatedVisit = visitRepository.save(visit);
            return VisitMapper.toResponse(updatedVisit);
        }).orElse(null);
    }

    @Override
    public List<VisitResponse> getAllVisits() {
        return visitRepository.findAll().stream()
                .map(VisitMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VisitResponse> getActiveVisits() {
        return visitRepository.findByExitTimeIsNull().stream()
                .map(VisitMapper::toResponse)
                .collect(Collectors.toList());
    }
}
