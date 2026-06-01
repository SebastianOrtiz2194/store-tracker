package com.store.tracker.service.impl;

import com.store.tracker.dto.VisitEntryRequest;
import com.store.tracker.dto.VisitLeaveRequest;
import com.store.tracker.dto.VisitResponse;
import com.store.tracker.entity.PurchasedItem;
import com.store.tracker.entity.Visit;
import com.store.tracker.exception.VisitNotFoundException;
import com.store.tracker.mapper.PurchasedItemMapper;
import com.store.tracker.mapper.VisitMapper;
import com.store.tracker.repository.VisitRepository;
import com.store.tracker.service.VisitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer implementation for visit management. Coordinates persistence
 * and mapping between {@link Visit} entities and the corresponding DTOs.
 */
@Service
public class VisitServiceImpl implements VisitService {

    private static final Logger log = LoggerFactory.getLogger(VisitServiceImpl.class);

    private final VisitRepository visitRepository;

    public VisitServiceImpl(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    /**
     * Creates a new visit record for a person entering the store.
     *
     * @param request the entry request carrying the visitor's name
     * @return the persisted visit as a response DTO
     */
    @Override
    @Transactional
    public VisitResponse registerEntry(VisitEntryRequest request) {
        log.info("Registering entry for: {}", request.personName());
        Visit visit = new Visit(request.personName(), LocalDateTime.now());
        Visit savedVisit = visitRepository.save(visit);
        log.debug("Entry saved with ID: {}", savedVisit.getId());
        return VisitMapper.toResponse(savedVisit);
    }

    /**
     * Closes an open visit by stamping its exit time and attaching the purchased
     * items reported in the request. Any items previously associated with the
     * visit are replaced by the new set.
     *
     * @param id      the identifier of the visit to close
     * @param request the leave request carrying the purchased items and total spent
     * @return the updated visit as a response DTO
     * @throws VisitNotFoundException if no visit exists with the given {@code id}
     */
    @Override
    @Transactional
    public VisitResponse registerExit(Long id, VisitLeaveRequest request) {
        log.info("Registering exit for visit ID: {}", id);
        return visitRepository.findById(id).map(visit -> {
            visit.setExitTime(LocalDateTime.now());

            // Clear existing items before attaching new ones
            int itemsCount = request.purchasedItems() != null ? request.purchasedItems().size() : 0;
            log.debug("Associating {} items with visit ID: {}", itemsCount, id);

            visit.getPurchasedItems().clear();

            // Map and attach new items
            if (request.purchasedItems() != null) {
                request.purchasedItems().forEach(itemDto -> {
                    PurchasedItem item = PurchasedItemMapper.toEntity(itemDto, visit);
                    visit.addPurchasedItem(item);
                });
            }

            visit.setTotalSpent(request.totalSpent());
            Visit updatedVisit = visitRepository.save(visit);
            log.info("Exit processed for visit ID: {}. Total: {}", id, updatedVisit.getTotalSpent());
            return VisitMapper.toResponse(updatedVisit);
        }).orElseThrow(() -> {
            log.warn("Failed to register exit: Visit ID {} not found", id);
            return new VisitNotFoundException("Visit not found with ID: " + id);
        });
    }

    /**
     * Returns every visit recorded in the system, both active and completed,
     * ordered as returned by the underlying repository.
     *
     * @return the list of visits; never {@code null}
     */
    @Override
    @Transactional(readOnly = true)
    public List<VisitResponse> getAllVisits() {
        return visitRepository.findAll().stream()
                .map(VisitMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Returns the visits whose visitors are still inside the store (i.e. their
     * {@code exitTime} is {@code null}).
     *
     * @return the list of active visits; never {@code null}
     */
    @Override
    @Transactional(readOnly = true)
    public List<VisitResponse> getActiveVisits() {
        log.debug("Fetching active visits (visitors still inside the store)");
        return visitRepository.findByExitTimeIsNull().stream()
                .map(VisitMapper::toResponse)
                .collect(Collectors.toList());
    }
}
