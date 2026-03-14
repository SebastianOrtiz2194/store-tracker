package com.store.tracker.service.impl;

import com.store.tracker.dto.VisitEntryRequest;
import com.store.tracker.dto.VisitLeaveRequest;
import com.store.tracker.dto.VisitResponse;
import com.store.tracker.entity.PurchasedItem;
import com.store.tracker.entity.Visit;
import com.store.tracker.exception.VisitNotFoundException;
import com.store.tracker.mapper.VisitMapper;
import com.store.tracker.repository.VisitRepository;
import com.store.tracker.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de la capa de servicio para Visit.
 */
@Service
public class VisitServiceImpl implements VisitService {

    @Autowired
    private VisitRepository visitRepository;

    @Override
    @Transactional
    public VisitResponse registerEntry(VisitEntryRequest request) {
        Visit visit = new Visit(request.getPersonName(), LocalDateTime.now());
        Visit savedVisit = visitRepository.save(visit);
        return VisitMapper.toResponse(savedVisit);
    }

    @Override
    @Transactional
    public VisitResponse registerExit(Long id, VisitLeaveRequest request) {
        return visitRepository.findById(id).map(visit -> {
            visit.setExitTime(LocalDateTime.now());
            
            // Limpiar items existentes si hubiera (aunque en este flujo solo se registran al salir)
            visit.getPurchasedItems().clear();
            
            // Mapear y añadir los nuevos items
            if (request.getPurchasedItems() != null) {
                request.getPurchasedItems().forEach(itemDto -> {
                    PurchasedItem item = VisitMapper.toItemEntity(itemDto, visit);
                    visit.addPurchasedItem(item);
                });
            }
            
            visit.setTotalSpent(request.getTotalSpent());
            Visit updatedVisit = visitRepository.save(visit);
            return VisitMapper.toResponse(updatedVisit);
        }).orElseThrow(() -> new VisitNotFoundException("No se encontró la visita con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitResponse> getAllVisits() {
        return visitRepository.findAll().stream()
                .map(VisitMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitResponse> getActiveVisits() {
        return visitRepository.findByExitTimeIsNull().stream()
                .map(VisitMapper::toResponse)
                .collect(Collectors.toList());
    }
}
