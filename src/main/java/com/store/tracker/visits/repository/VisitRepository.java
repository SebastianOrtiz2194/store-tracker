package com.store.tracker.visits.repository;

import com.store.tracker.visits.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA repository for {@link Visit} entities.
 */
@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

    /**
     * Returns all visits whose {@code exitTime} is {@code null}, meaning the
     * visitor is still inside the store.
     *
     * @return the list of active visits; never {@code null}
     */
    List<Visit> findByExitTimeIsNull();

    /**
     * Returns all visits whose {@code entryTime} falls within the inclusive
     * range {@code [from, to]}.
     *
     * @param from the lower bound of the entry time range
     * @param to   the upper bound of the entry time range
     * @return the list of visits in the range; never {@code null}
     */
    List<Visit> findByEntryTimeBetween(LocalDateTime from, LocalDateTime to);
}
