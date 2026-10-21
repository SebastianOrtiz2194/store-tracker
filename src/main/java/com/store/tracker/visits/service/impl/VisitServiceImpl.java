package com.store.tracker.visits.service.impl;

import com.store.tracker.dto.PageResponse;
import com.store.tracker.exception.InvalidDateRangeException;
import com.store.tracker.exception.VisitNotFoundException;
import com.store.tracker.visits.dto.VisitEntryRequest;
import com.store.tracker.visits.dto.VisitLeaveRequest;
import com.store.tracker.visits.dto.VisitResponse;
import com.store.tracker.visits.dto.VisitSummaryResponse;
import com.store.tracker.visits.entity.PurchasedItem;
import com.store.tracker.visits.entity.Visit;
import com.store.tracker.visits.mapper.PurchasedItemMapper;
import com.store.tracker.visits.mapper.VisitMapper;
import com.store.tracker.visits.mapper.VisitSummaryMapper;
import com.store.tracker.visits.repository.VisitRepository;
import com.store.tracker.visits.repository.VisitSpecifications;
import com.store.tracker.visits.service.VisitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        Visit visit = visitRepository.findWithItemsById(id)
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
        return visitRepository.findWithItemsById(id)
                .map(VisitMapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("Visit not found with ID: {}", id);
                    return new VisitNotFoundException(id);
                });
    }

    /**
     * Returns a page of the visit history, optionally filtered by an inclusive
     * {@code entryTime} range. Either bound may be omitted; supplying both
     * requires {@code from <= to}.
     *
     * <p>The result is a summary projection: purchased items are not loaded, so
     * the number of SQL statements does not grow with the page size.
     *
     * @param from     the inclusive lower bound of the entry time range, or {@code null}
     * @param to       the inclusive upper bound of the entry time range, or {@code null}
     * @param pageable the requested page and sort
     * @return a page of visit summaries; never {@code null}
     * @throws InvalidDateRangeException if both bounds are present and {@code from > to}
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<VisitSummaryResponse> getAllVisits(LocalDateTime from, LocalDateTime to, Pageable pageable) {
        log.debug("Fetching visits with from={}, to={}, page={}, size={}",
                from, to, pageable.getPageNumber(), pageable.getPageSize());

        if (from != null && to != null && from.isAfter(to)) {
            log.warn("Rejecting inverted date range from={}, to={}", from, to);
            throw new InvalidDateRangeException(from, to);
        }

        Pageable effective = withStableSort(pageable);
        Page<Visit> page = visitRepository.findAll(VisitSpecifications.enteredBetween(from, to), effective);
        return PageResponse.from(page.map(VisitSummaryMapper::toResponse));
    }

    /**
     * Adds a deterministic tie-breaker so rows do not drift between pages when
     * several visits share the same entry time.
     */
    private Pageable withStableSort(Pageable pageable) {
        Sort sort = pageable.getSort().isSorted()
                ? pageable.getSort()
                : Sort.by(Sort.Direction.DESC, "entryTime");
        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort.and(Sort.by(Sort.Direction.DESC, "id")));
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
        return visitRepository.findActiveWithItems().stream()
                .map(VisitMapper::toResponse)
                .toList();
    }
}
