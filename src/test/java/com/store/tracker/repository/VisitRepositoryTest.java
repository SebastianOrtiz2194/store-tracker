package com.store.tracker.repository;

import com.store.tracker.entity.Visit;
import com.store.tracker.config.JpaConfig;
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
@ActiveProfiles("dev")
public class VisitRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VisitRepository visitRepository;

    @Test
    void whenFindByExitTimeIsNull_thenReturnActiveVisits() {
        // given
        Visit activeVisit = new Visit("Carlos", LocalDateTime.now());
        entityManager.persist(activeVisit);

        Visit finishedVisit = new Visit("Ana", LocalDateTime.now().minusHours(2));
        finishedVisit.setExitTime(LocalDateTime.now());
        entityManager.persist(finishedVisit);

        entityManager.flush();

        // when
        List<Visit> foundVisits = visitRepository.findByExitTimeIsNull();

        // then
        assertThat(foundVisits)
                .hasSize(1)
                .extracting(Visit::getPersonName)
                .containsExactly("Carlos");

        assertThat(foundVisits.get(0).getExitTime()).isNull();
    }

    @Test
    void whenFindByExitTimeIsNullAndNoVisitsExist_thenReturnEmptyList() {
        // when
        List<Visit> foundVisits = visitRepository.findByExitTimeIsNull();

        // then
        assertThat(foundVisits)
                .isNotNull()
                .isEmpty();
    }

    @Test
    void whenFindAll_thenReturnAllPersistedVisits() {
        // given
        Visit firstVisit = new Visit("Alice", LocalDateTime.now().minusHours(3));
        firstVisit.setExitTime(LocalDateTime.now().minusHours(2));
        entityManager.persist(firstVisit);

        Visit secondVisit = new Visit("Bob", LocalDateTime.now().minusHours(1));
        entityManager.persist(secondVisit);

        entityManager.flush();

        // when
        List<Visit> allVisits = visitRepository.findAll();

        // then
        assertThat(allVisits)
                .hasSize(2)
                .extracting(Visit::getPersonName)
                .containsExactlyInAnyOrder("Alice", "Bob");
    }

    @Test
    void whenFindAllAndNoVisitsExist_thenReturnEmptyList() {
        // when
        List<Visit> allVisits = visitRepository.findAll();

        // then
        assertThat(allVisits)
                .isNotNull()
                .isEmpty();
    }

    @Test
    void whenSave_thenPersistVisitWithGeneratedId() {
        // given
        Visit newVisit = new Visit("Lucia", LocalDateTime.now());
        assertThat(newVisit.getId()).isNull();

        // when
        Visit savedVisit = visitRepository.save(newVisit);
        entityManager.flush();

        // then
        assertThat(savedVisit.getId())
                .isNotNull()
                .isPositive();

        Optional<Visit> found = visitRepository.findById(savedVisit.getId());
        assertThat(found)
                .isPresent()
                .get()
                .extracting(Visit::getPersonName)
                .isEqualTo("Lucia");
    }
}
