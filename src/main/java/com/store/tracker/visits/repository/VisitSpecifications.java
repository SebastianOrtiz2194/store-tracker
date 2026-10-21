package com.store.tracker.visits.repository;

import com.store.tracker.visits.entity.Visit;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Reusable query predicates for {@link Visit}.
 *
 * <p>Building the predicate from whichever bounds are present is what makes a
 * one-sided {@code from}/{@code to} filter behave correctly instead of being
 * silently ignored.
 */
public final class VisitSpecifications {

    private VisitSpecifications() {
    }

    /**
     * Matches visits whose entry time falls inside the inclusive range.
     * Either bound may be {@code null}, in which case that side is unbounded.
     *
     * @param from the inclusive lower bound, or {@code null}
     * @param to   the inclusive upper bound, or {@code null}
     * @return a specification matching the requested range
     */
    public static Specification<Visit> enteredBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>(2);
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("entryTime"), from));
            }
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("entryTime"), to));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
