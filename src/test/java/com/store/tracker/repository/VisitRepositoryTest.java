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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class) // Habilita auditoría JPA
@ActiveProfiles("dev") // Utilizar H2 en memoria
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
        assertThat(foundVisits).hasSize(1);
        assertThat(foundVisits.get(0).getPersonName()).isEqualTo("Carlos");
        assertThat(foundVisits.get(0).getExitTime()).isNull();
    }
}
