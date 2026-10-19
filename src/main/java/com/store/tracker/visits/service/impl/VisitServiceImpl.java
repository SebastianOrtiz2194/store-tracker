package com.store.tracker.visits.service.impl;

import com.store.tracker.exception.VisitNotFoundException;
import com.store.tracker.visits.dto.VisitEntryRequest;
import com.store.tracker.visits.dto.VisitLeaveRequest;
import com.store.tracker.visits.dto.VisitResponse;
import com.store.tracker.visits.entity.PurchasedItem;
import com.store.tracker.visits.entity.Visit;
import com.store.tracker.visits.mapper.PurchasedItemMapper;
import com.store.tracker.visits.mapper.VisitMapper;
import com.store.tracker.visits.repository.VisitRepository;
import com.store.tracker.visits.service.VisitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service layer implementation for visit management. Coordinates persistence
 * and mapping between {@link Visit} entities and the corresponding DTOs.
 *
 * <p>All business rules live in the {@link Visit} aggregate; this class only
 * loads, delegates, persists and maps.
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
     * items reported in the request. The total spent is derived from those items.
     *
     * @param id      the identifier of the visit to close
     * @param request the leave request carrying the purchased items
     * @return the updated visit as a response DTO
     * @throws VisitNotFoundException       if no visit exists with the given {@code id}
     * @throws com.store.tracker.exception.VisitAlreadyClosedException if the visit is already closed
     */
    @Override
    @Transactional
    public VisitResponse registerExit(Long id, VisitLeaveRequest request) {
        log.info("Registering exit for visit ID: {}", id);
        Visit visit = visitRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Failed to register exit: Visit ID {} not found", id);
                    return new VisitNotFoundException(id);
                });

        List<PurchasedItem> items = request.purchasedItems().stream()
                .map(PurchasedItemMapper::toEntity)
                .toList();
        log.debug("Associating {} items with visit ID: {}", items.size(), id);

        visit.close(LocalDateTime.now(), items);
        Visit updatedVisit = visitRepository.save(visit);
        log.info("Exit processed for visit ID: {} total={}", id, updatedVisit.getTotalSpent());
        return VisitMapper.toResponse(updatedVisit);
    }

    /**
     * Fetches a single visit by its identifier.
     *
     * @param id the identifier of the visit to retrieve
     * @return the visit as a response DTO
     * @throws VisitNotFoundException if no visit exists with the given {@code id}
     */
    @Override
    @Transactional(readOnly = true)
    public VisitResponse getVisitById(Long id) {
        log.debug("Fetching visit with ID: {}", id);
        return visitRepository.findById(id)
                .map(VisitMapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("Visit not found with ID: {}", id);
                    return new VisitNotFoundException(id);
                });
    }

    /**
     * Returns every visit recorded in the system, both active and completed,
     * optionally filtered by an inclusive {@code entryTime} range.
     *
     * @param from the inclusive lower bound of the entry time range, or {@code null} for no lower bound
     * @param to   the inclusive upper bound of the entry time range, or {@code null} for no upper bound
     * @return the list of visits; never {@code null}
     */
    @Override
    @Transactional(readOnly = true)
    public List<VisitResponse> getAllVisits(LocalDateTime from, LocalDateTime to) {
        log.debug("Fetching visits with from={}, to={}", from, to);
        List<Visit> visits = (from != null && to != null)
                ? visitRepository.findByEntryTimeBetween(from, to)
                : visitRepository.findAll();
        return visits.stream()
                .map(VisitMapper::toResponse)
                .toList();
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
                .toList();
    }
}
