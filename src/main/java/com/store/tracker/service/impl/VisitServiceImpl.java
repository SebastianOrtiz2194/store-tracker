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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(VisitServiceImpl.class);

    @Autowired
    private VisitRepository visitRepository;

    @Override
    @Transactional
    public VisitResponse registerEntry(VisitEntryRequest request) {
        log.info("Registrando entrada para: {}", request.getPersonName());
        Visit visit = new Visit(request.getPersonName(), LocalDateTime.now());
        Visit savedVisit = visitRepository.save(visit);
        log.debug("Entrada guardada con ID: {}", savedVisit.getId());
        return VisitMapper.toResponse(savedVisit);
    }

    @Override
    @Transactional
    public VisitResponse registerExit(Long id, VisitLeaveRequest request) {
        log.info("Registrando salida para visita ID: {}", id);
        return visitRepository.findById(id).map(visit -> {
            visit.setExitTime(LocalDateTime.now());
            
            // Limpiar items existentes si hubiera (aunque en este flujo solo se registran al salir)
            int itemsCount = request.getPurchasedItems() != null ? request.getPurchasedItems().size() : 0;
            log.debug("Asociando {} articulos a la visita ID: {}", itemsCount, id);

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
            log.info("Salida procesada exitosamente para visita ID: {}. Total: {}", id, updatedVisit.getTotalSpent());
            return VisitMapper.toResponse(updatedVisit);
        }).orElseThrow(() -> {
            log.warn("Intento de registro de salida fallido: Visita ID {} no encontrada", id);
            return new VisitNotFoundException("No se encontró la visita con ID: " + id);
        });
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
        log.debug("Consultando visitas activas (personas dentro del establecimiento)");
        return visitRepository.findByExitTimeIsNull().stream()
                .map(VisitMapper::toResponse)
                .collect(Collectors.toList());
    }
}
