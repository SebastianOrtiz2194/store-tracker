package com.store.tracker.visits.repository;

import com.store.tracker.visits.entity.Visit;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Visit} entities.
 *
 * <p>List queries deliberately return summary projections or avoid touching the
 * lazily loaded items; only the dedicated detail queries fetch them, so the
 * number of SQL statements stays constant regardless of page size.
 */
@Repository
public interface VisitRepository extends JpaRepository<Visit, Long>, JpaSpecificationExecutor<Visit> {

    /**
     * Returns the visits that are still open, with their purchased items fetched
     * in the same query.
     *
     * @return the list of active visits; never {@code null}
     */
    @EntityGraph(attributePaths = "purchasedItems")
    @Query("select distinct v from Visit v where v.exitTime is null")
    List<Visit> findActiveWithItems();

    /**
     * Returns a single visit with its purchased items fetched eagerly, so the
     * mapping to the full response never triggers an extra query.
     *
     * @param id the visit identifier
     * @return the visit, or empty when it does not exist
     */
    @EntityGraph(attributePaths = "purchasedItems")
    @Query("select v from Visit v where v.id = :id")
    Optional<Visit> findWithItemsById(@Param("id") Long id);
}
