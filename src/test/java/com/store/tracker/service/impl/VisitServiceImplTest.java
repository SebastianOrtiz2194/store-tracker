package com.store.tracker.service.impl;

import com.store.tracker.dto.VisitEntryRequest;
import com.store.tracker.dto.VisitLeaveRequest;
import com.store.tracker.dto.VisitResponse;
import com.store.tracker.entity.Visit;
import com.store.tracker.exception.VisitNotFoundException;
import com.store.tracker.repository.VisitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        // Arrange
        VisitEntryRequest request = new VisitEntryRequest();
        request.setPersonName("Juan Perez");
        
        when(visitRepository.save(any(Visit.class))).thenReturn(mockVisit);

        // Act
        VisitResponse response = visitService.registerEntry(request);

        // Assert
        assertNotNull(response);
        assertEquals("Juan Perez", response.getPersonName());
        verify(visitRepository, times(1)).save(any(Visit.class));
    }

    @Test
    void registerExit_WhenVisitExists_ShouldReturnUpdatedVisitResponse() {
        // Arrange
        VisitLeaveRequest request = new VisitLeaveRequest();
        request.setTotalSpent(150.0);
        
        when(visitRepository.findById(1L)).thenReturn(Optional.of(mockVisit));
        when(visitRepository.save(any(Visit.class))).thenReturn(mockVisit);

        // Act
        VisitResponse response = visitService.registerExit(1L, request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getExitTime());
        assertEquals(150.0, response.getTotalSpent());
        verify(visitRepository, times(1)).findById(1L);
        verify(visitRepository, times(1)).save(any(Visit.class));
    }

    @Test
    void registerExit_WhenVisitDoesNotExist_ShouldThrowException() {
        // Arrange
        VisitLeaveRequest request = new VisitLeaveRequest();
        
        when(visitRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(VisitNotFoundException.class, () -> visitService.registerExit(99L, request));
        verify(visitRepository, times(1)).findById(99L);
        verify(visitRepository, never()).save(any(Visit.class));
    }
}
