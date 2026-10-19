package com.store.tracker.visits.service.impl;

import com.store.tracker.exception.InvalidDateRangeException;
import com.store.tracker.exception.VisitAlreadyClosedException;
import com.store.tracker.exception.VisitNotFoundException;
import com.store.tracker.visits.dto.PurchasedItemDto;
import com.store.tracker.visits.dto.VisitEntryRequest;
import com.store.tracker.visits.dto.VisitLeaveRequest;
import com.store.tracker.visits.dto.VisitResponse;
import com.store.tracker.visits.entity.Visit;
import com.store.tracker.visits.repository.VisitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisitServiceImplTest {

    @Mock
    private VisitRepository visitRepository;

    @InjectMocks
    private VisitServiceImpl visitService;

    private Visit openVisit;

    @BeforeEach
    void setUp() {
        openVisit = new Visit("Juan Perez", LocalDateTime.now().minusHours(1));
        openVisit.setId(1L);
    }

    @Test
    void registerEntry_ShouldReturnVisitResponse() {
        when(visitRepository.save(any(Visit.class))).thenReturn(openVisit);

        VisitResponse response = visitService.registerEntry(new VisitEntryRequest("Juan Perez"));

        assertThat(response)
                .isNotNull()
                .extracting(VisitResponse::personName)
                .isEqualTo("Juan Perez");
        assertThat(response.totalSpent()).isNull();

        verify(visitRepository).save(any(Visit.class));
    }

    @Test
    void registerExit_ShouldDeriveTotalFromLineItems() {
        when(visitRepository.findById(1L)).thenReturn(Optional.of(openVisit));
        when(visitRepository.save(any(Visit.class))).thenReturn(openVisit);

        VisitLeaveRequest request = new VisitLeaveRequest(List.of(
                new PurchasedItemDto(null, "Coffee", new BigDecimal("3.50"), 2),
                new PurchasedItemDto(null, "Croissant", new BigDecimal("2.00"), 1)));

        VisitResponse response = visitService.registerExit(1L, request);

        assertThat(response.exitTime()).isNotNull();
        assertThat(response.totalSpent()).isEqualByComparingTo("9.00");
        assertThat(response.purchasedItems()).hasSize(2);
        assertThat(response.purchasedItems())
                .extracting(PurchasedItemDto::name)
                .containsExactly("Coffee", "Croissant");

        verify(visitRepository).findById(1L);
        verify(visitRepository).save(any(Visit.class));
    }

    @Test
    void registerExit_WithEmptyItems_ShouldTotalZero() {
        when(visitRepository.findById(1L)).thenReturn(Optional.of(openVisit));
        when(visitRepository.save(any(Visit.class))).thenReturn(openVisit);

        VisitResponse response = visitService.registerExit(1L, new VisitLeaveRequest(List.of()));

        assertThat(response.exitTime()).isNotNull();
        assertThat(response.totalSpent()).isEqualByComparingTo("0.00");
        assertThat(response.purchasedItems()).isEmpty();
    }

    @Test
    void registerExit_WhenAlreadyClosed_ShouldThrowAndNotSave() {
        openVisit.close(LocalDateTime.now(), List.of());
        when(visitRepository.findById(1L)).thenReturn(Optional.of(openVisit));

        assertThatThrownBy(() -> visitService.registerExit(1L, new VisitLeaveRequest(List.of())))
                .isInstanceOf(VisitAlreadyClosedException.class)
                .hasMessageContaining("1");

        verify(visitRepository).findById(1L);
        verify(visitRepository, never()).save(any(Visit.class));
    }

    @Test
    void registerExit_WhenVisitDoesNotExist_ShouldThrow() {
        when(visitRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> visitService.registerExit(99L, new VisitLeaveRequest(List.of())))
                .isInstanceOf(VisitNotFoundException.class)
                .hasMessageContaining("99");

        verify(visitRepository).findById(99L);
        verify(visitRepository, never()).save(any(Visit.class));
    }

    @Test
    void getVisitById_WhenVisitExists_ShouldReturnVisitResponse() {
        when(visitRepository.findById(1L)).thenReturn(Optional.of(openVisit));

        VisitResponse response = visitService.getVisitById(1L);

        assertThat(response)
                .isNotNull()
                .extracting(VisitResponse::personName)
                .isEqualTo("Juan Perez");

        verify(visitRepository).findById(1L);
    }

    @Test
    void getVisitById_WhenVisitDoesNotExist_ShouldThrowException() {
        when(visitRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> visitService.getVisitById(99L))
                .isInstanceOf(VisitNotFoundException.class)
                .hasMessageContaining("99");

        verify(visitRepository).findById(99L);
    }

    @Test
    void getAllVisits_WithoutBounds_ShouldReturnAll() {
        when(visitRepository.findAll()).thenReturn(List.of(openVisit));

        List<VisitResponse> responses = visitService.getAllVisits(null, null);

        assertThat(responses).hasSize(1);
        verify(visitRepository).findAll();
        verify(visitRepository, never()).findByEntryTimeBetween(any(), any());
    }

    @Test
    void getAllVisits_WithFromOnly_ShouldApplyLowerBound() {
        LocalDateTime from = LocalDateTime.of(2024, 1, 1, 0, 0);
        when(visitRepository.findByEntryTimeGreaterThanEqual(from)).thenReturn(List.of(openVisit));

        List<VisitResponse> responses = visitService.getAllVisits(from, null);

        assertThat(responses).hasSize(1);
        verify(visitRepository).findByEntryTimeGreaterThanEqual(from);
        verify(visitRepository, never()).findAll();
        verify(visitRepository, never()).findByEntryTimeLessThanEqual(any());
    }

    @Test
    void getAllVisits_WithToOnly_ShouldApplyUpperBound() {
        LocalDateTime to = LocalDateTime.of(2024, 12, 31, 23, 59);
        when(visitRepository.findByEntryTimeLessThanEqual(to)).thenReturn(List.of(openVisit));

        List<VisitResponse> responses = visitService.getAllVisits(null, to);

        assertThat(responses).hasSize(1);
        verify(visitRepository).findByEntryTimeLessThanEqual(to);
        verify(visitRepository, never()).findAll();
        verify(visitRepository, never()).findByEntryTimeGreaterThanEqual(any());
    }

    @Test
    void getAllVisits_WithBothBounds_ShouldUseRange() {
        LocalDateTime from = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 12, 31, 23, 59);
        when(visitRepository.findByEntryTimeBetween(from, to)).thenReturn(List.of(openVisit));

        List<VisitResponse> responses = visitService.getAllVisits(from, to);

        assertThat(responses).hasSize(1);
        verify(visitRepository).findByEntryTimeBetween(from, to);
        verify(visitRepository, never()).findAll();
    }

    @Test
    void getAllVisits_WithInvertedRange_ShouldRejectWithoutQuerying() {
        LocalDateTime from = LocalDateTime.of(2024, 2, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 1, 1, 0, 0);

        assertThatThrownBy(() -> visitService.getAllVisits(from, to))
                .isInstanceOf(InvalidDateRangeException.class)
                .hasMessageContaining("from");

        verifyNoInteractions(visitRepository);
    }

    @Test
    void getActiveVisits_ShouldReturnOnlyVisitsWithoutExitTime() {
        Visit activeVisit = new Visit("Active Visitor", LocalDateTime.now());
        activeVisit.setId(10L);
        when(visitRepository.findByExitTimeIsNull()).thenReturn(List.of(activeVisit));

        List<VisitResponse> responses = visitService.getActiveVisits();

        assertThat(responses)
                .hasSize(1)
                .extracting(VisitResponse::personName)
                .containsExactly("Active Visitor");

        verify(visitRepository).findByExitTimeIsNull();
    }

    @Test
    void getActiveVisits_WhenNoActiveVisitsExist_ShouldReturnEmptyList() {
        when(visitRepository.findByExitTimeIsNull()).thenReturn(List.of());

        List<VisitResponse> responses = visitService.getActiveVisits();

        assertThat(responses).isNotNull().isEmpty();
        verify(visitRepository).findByExitTimeIsNull();
    }
}
