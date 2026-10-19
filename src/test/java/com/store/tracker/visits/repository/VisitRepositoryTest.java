package com.store.tracker.visits.repository;

import com.store.tracker.config.JpaConfig;
import com.store.tracker.visits.entity.Visit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
@ActiveProfiles("test")
class VisitRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VisitRepository visitRepository;

    @Test
    void whenFindByExitTimeIsNull_thenReturnActiveVisits() {
        Visit activeVisit = new Visit("Carlos", LocalDateTime.now());
        entityManager.persist(activeVisit);

        Visit finishedVisit = new Visit("Ana", LocalDateTime.now().minusHours(2));
        finishedVisit.close(LocalDateTime.now().minusHours(1), List.of());
        entityManager.persist(finishedVisit);

        entityManager.flush();

        List<Visit> foundVisits = visitRepository.findByExitTimeIsNull();

        assertThat(foundVisits)
                .hasSize(1)
                .extracting(Visit::getPersonName)
                .containsExactly("Carlos");

        assertThat(foundVisits.get(0).isClosed()).isFalse();
    }

    @Test
    void whenFindByExitTimeIsNullAndNoVisitsExist_thenReturnEmptyList() {
        List<Visit> foundVisits = visitRepository.findByExitTimeIsNull();

        assertThat(foundVisits).isNotNull().isEmpty();
    }

    @Test
    void whenFindAll_thenReturnAllPersistedVisits() {
        LocalDateTime now = LocalDateTime.now();

        Visit firstVisit = new Visit("Alice", now.minusHours(3));
        firstVisit.close(now.minusHours(2), List.of());
        entityManager.persist(firstVisit);

        Visit secondVisit = new Visit("Bob", now.minusHours(1));
        entityManager.persist(secondVisit);

        entityManager.flush();

        List<Visit> allVisits = visitRepository.findAll();

        assertThat(allVisits)
                .hasSize(2)
                .extracting(Visit::getPersonName)
                .containsExactlyInAnyOrder("Alice", "Bob");
    }

    @Test
    void whenFindAllAndNoVisitsExist_thenReturnEmptyList() {
        List<Visit> allVisits = visitRepository.findAll();

        assertThat(allVisits).isNotNull().isEmpty();
    }

    @Test
    void whenSave_thenPersistVisitWithGeneratedId() {
        Visit newVisit = new Visit("Lucia", LocalDateTime.now());
        assertThat(newVisit.getId()).isNull();

        Visit savedVisit = visitRepository.save(newVisit);
        entityManager.flush();

        assertThat(savedVisit.getId()).isNotNull().isPositive();

        Optional<Visit> found = visitRepository.findById(savedVisit.getId());
        assertThat(found)
                .isPresent()
                .get()
                .extracting(Visit::getPersonName)
                .isEqualTo("Lucia");
    }

    @Test
    void whenFindByEntryTimeGreaterThanEqual_thenReturnVisitsFromBoundOnward() {
        LocalDateTime base = LocalDateTime.of(2024, 6, 1, 12, 0);
        persist("Early", base.minusDays(1));
        persist("Boundary", base);
        persist("Late", base.plusDays(1));
        entityManager.flush();

        List<Visit> found = visitRepository.findByEntryTimeGreaterThanEqual(base);

        assertThat(found)
                .extracting(Visit::getPersonName)
                .containsExactlyInAnyOrder("Boundary", "Late");
    }

    @Test
    void whenFindByEntryTimeLessThanEqual_thenReturnVisitsUpToBound() {
        LocalDateTime base = LocalDateTime.of(2024, 6, 1, 12, 0);
        persist("Early", base.minusDays(1));
        persist("Boundary", base);
        persist("Late", base.plusDays(1));
        entityManager.flush();

        List<Visit> found = visitRepository.findByEntryTimeLessThanEqual(base);

        assertThat(found)
                .extracting(Visit::getPersonName)
                .containsExactlyInAnyOrder("Early", "Boundary");
    }

    @Test
    void whenFindByEntryTimeBetween_thenReturnOnlyVisitsInsideRange() {
        LocalDateTime from = LocalDateTime.of(2024, 6, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 6, 30, 23, 59);
        persist("Before", from.minusDays(1));
        persist("Inside", from.plusDays(10));
        persist("After", to.plusDays(1));
        entityManager.flush();

        List<Visit> found = visitRepository.findByEntryTimeBetween(from, to);

        assertThat(found)
                .extracting(Visit::getPersonName)
                .containsExactly("Inside");
    }

    private void persist(String personName, LocalDateTime entryTime) {
        entityManager.persist(new Visit(personName, entryTime));
    }
}
