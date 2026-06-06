package com.store.tracker.visits.service.impl;

import com.store.tracker.visits.dto.VisitEntryRequest;
import com.store.tracker.visits.dto.VisitLeaveRequest;
import com.store.tracker.visits.dto.VisitResponse;
import com.store.tracker.visits.entity.Visit;
import com.store.tracker.exception.VisitNotFoundException;
import com.store.tracker.visits.repository.VisitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VisitServiceImplTest {

    @Mock
    private VisitRepository visitRepository;

    @InjectMocks
    private VisitServiceImpl visitService;

    private Visit mockVisit;

    @BeforeEach
    void setUp() {
        mockVisit = new Visit("Juan Perez", LocalDateTime.now());
        mockVisit.setId(1L);
    }

    @Test
    void registerEntry_ShouldReturnVisitResponse() {
        // given
        VisitEntryRequest request = new VisitEntryRequest("Juan Perez");
        when(visitRepository.save(any(Visit.class))).thenReturn(mockVisit);

        // when
        VisitResponse response = visitService.registerEntry(request);

        // then
        assertThat(response)
                .isNotNull()
                .extracting(VisitResponse::personName)
                .isEqualTo("Juan Perez");

        verify(visitRepository, times(1)).save(any(Visit.class));
    }

    @Test
    void registerExit_WhenVisitExists_ShouldReturnUpdatedVisitResponse() {
        // given
        VisitLeaveRequest request = new VisitLeaveRequest(null, 150.0);
        when(visitRepository.findById(1L)).thenReturn(Optional.of(mockVisit));
        when(visitRepository.save(any(Visit.class))).thenReturn(mockVisit);

        // when
        VisitResponse response = visitService.registerExit(1L, request);

        // then
        assertThat(response)
                .isNotNull()
                .satisfies(r -> {
                    assertThat(r.exitTime()).isNotNull();
                    assertThat(r.totalSpent()).isEqualTo(150.0);
                });

        verify(visitRepository, times(1)).findById(1L);
        verify(visitRepository, times(1)).save(any(Visit.class));
    }

    @Test
    void registerExit_WithEmptyItemsAndZeroTotal_ShouldCompleteExit() {
        // given
        VisitLeaveRequest request = new VisitLeaveRequest(List.of(), 0.0);
        when(visitRepository.findById(1L)).thenReturn(Optional.of(mockVisit));
        when(visitRepository.save(any(Visit.class))).thenReturn(mockVisit);

        // when
        VisitResponse response = visitService.registerExit(1L, request);

        // then
        assertThat(response)
                .isNotNull()
                .satisfies(r -> {
                    assertThat(r.exitTime()).isNotNull();
                    assertThat(r.totalSpent()).isZero();
                    assertThat(r.purchasedItems()).isEmpty();
                });

        verify(visitRepository, times(1)).findById(1L);
        verify(visitRepository, times(1)).save(any(Visit.class));
    }

    @Test
    void getVisitById_WhenVisitExists_ShouldReturnVisitResponse() {
        // given
        when(visitRepository.findById(1L)).thenReturn(Optional.of(mockVisit));

        // when
        VisitResponse response = visitService.getVisitById(1L);

        // then
        assertThat(response)
                .isNotNull()
                .extracting(VisitResponse::personName)
                .isEqualTo("Juan Perez");

        verify(visitRepository, times(1)).findById(1L);
    }

    @Test
    void getVisitById_WhenVisitDoesNotExist_ShouldThrowException() {
        // given
        when(visitRepository.findById(99L)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> visitService.getVisitById(99L))
                .isInstanceOf(VisitNotFoundException.class)
                .hasMessageContaining("99");

        verify(visitRepository, times(1)).findById(99L);
    }

    @Test
    void registerExit_WhenVisitDoesNotExist_ShouldThrowException() {
        // given
        VisitLeaveRequest request = new VisitLeaveRequest(null, null);
        when(visitRepository.findById(99L)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> visitService.registerExit(99L, request))
                .isInstanceOf(VisitNotFoundException.class);

        verify(visitRepository, times(1)).findById(99L);
        verify(visitRepository, never()).save(any(Visit.class));
    }

    @Test
    void getAllVisits_ShouldReturnMappedVisitResponses() {
        // given
        Visit secondVisit = new Visit("Maria Gomez", LocalDateTime.now().minusHours(1));
        secondVisit.setId(2L);

        when(visitRepository.findAll()).thenReturn(List.of(mockVisit, secondVisit));

        // when
        List<VisitResponse> responses = visitService.getAllVisits(null, null);

        // then
        assertThat(responses)
                .hasSize(2)
                .extracting(VisitResponse::personName)
                .containsExactly("Juan Perez", "Maria Gomez");

        verify(visitRepository, times(1)).findAll();
    }

    @Test
    void getAllVisits_WhenNoVisitsExist_ShouldReturnEmptyList() {
        // given
        when(visitRepository.findAll()).thenReturn(List.of());

        // when
        List<VisitResponse> responses = visitService.getAllVisits(null, null);

        // then
        assertThat(responses)
                .isNotNull()
                .isEmpty();

        verify(visitRepository, times(1)).findAll();
    }

    @Test
    void getAllVisits_WithDateRange_ShouldFilterByEntryTime() {
        // given
        LocalDateTime from = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 12, 31, 23, 59);
        when(visitRepository.findByEntryTimeBetween(from, to)).thenReturn(List.of(mockVisit));

        // when
        List<VisitResponse> responses = visitService.getAllVisits(from, to);

        // then
        assertThat(responses)
                .hasSize(1)
                .extracting(VisitResponse::personName)
                .containsExactly("Juan Perez");

        verify(visitRepository, times(1)).findByEntryTimeBetween(from, to);
        verify(visitRepository, never()).findAll();
    }

    @Test
    void getActiveVisits_ShouldReturnOnlyVisitsWithoutExitTime() {
        // given
        Visit activeVisit = new Visit("Active Visitor", LocalDateTime.now());
        activeVisit.setId(10L);

        when(visitRepository.findByExitTimeIsNull()).thenReturn(List.of(activeVisit));

        // when
        List<VisitResponse> responses = visitService.getActiveVisits();

        // then
        assertThat(responses)
                .hasSize(1)
                .extracting(VisitResponse::personName)
                .containsExactly("Active Visitor");

        verify(visitRepository, times(1)).findByExitTimeIsNull();
    }

    @Test
    void getActiveVisits_WhenNoActiveVisitsExist_ShouldReturnEmptyList() {
        // given
        when(visitRepository.findByExitTimeIsNull()).thenReturn(List.of());

        // when
        List<VisitResponse> responses = visitService.getActiveVisits();

        // then
        assertThat(responses)
                .isNotNull()
                .isEmpty();

        verify(visitRepository, times(1)).findByExitTimeIsNull();
    }
}
