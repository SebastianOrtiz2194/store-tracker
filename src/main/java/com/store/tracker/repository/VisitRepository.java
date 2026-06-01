package com.store.tracker.repository;

import com.store.tracker.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
