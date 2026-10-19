package com.store.tracker.visits.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.tracker.config.SecurityConfig;
import com.store.tracker.exception.InvalidDateRangeException;
import com.store.tracker.exception.VisitAlreadyClosedException;
import com.store.tracker.exception.VisitNotFoundException;
import com.store.tracker.visits.dto.PurchasedItemDto;
import com.store.tracker.visits.dto.VisitEntryRequest;
import com.store.tracker.visits.dto.VisitLeaveRequest;
import com.store.tracker.visits.dto.VisitResponse;
import com.store.tracker.visits.service.VisitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VisitController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class VisitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VisitService visitService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void registerEntry_WhenValidRequest_ShouldReturn201WithLocation() throws Exception {
        when(visitService.registerEntry(any(VisitEntryRequest.class)))
                .thenReturn(new VisitResponse(1L, "Maria Gomez"));

        mockMvc.perform(post("/api/visits/enter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new VisitEntryRequest("Maria Gomez"))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/visits/1"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Entry registered successfully"))
                .andExpect(jsonPath("$.data.personName").value("Maria Gomez"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void registerEntry_WhenInvalidRequest_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/visits/enter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new VisitEntryRequest(""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data.personName").exists());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void registerEntry_WhenNameTooLong_ShouldReturn400() throws Exception {
        String longName = "a".repeat(121);

        mockMvc.perform(post("/api/visits/enter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new VisitEntryRequest(longName))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void registerEntry_WhenUnauthorized_ShouldReturn401() throws Exception {
        mockMvc.perform(post("/api/visits/enter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new VisitEntryRequest("Unauthenticated User"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void registerExit_WhenValidRequest_ShouldReturn200WithDerivedTotal() throws Exception {
        PurchasedItemDto item = new PurchasedItemDto(1L, "Coffee", new BigDecimal("3.50"), 2);
        VisitResponse response = new VisitResponse(
                1L, "Juan Perez", LocalDateTime.now().minusHours(1), LocalDateTime.now(),
                List.of(item), new BigDecimal("7.00"));
        when(visitService.registerExit(anyLong(), any(VisitLeaveRequest.class))).thenReturn(response);

        VisitLeaveRequest request = new VisitLeaveRequest(List.of(
                new PurchasedItemDto(null, "Coffee", new BigDecimal("3.50"), 2)));

        mockMvc.perform(put("/api/visits/1/leave")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Exit registered successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.totalSpent").value(7.0));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void registerExit_WhenVisitNotFound_ShouldReturn404() throws Exception {
        when(visitService.registerExit(anyLong(), any(VisitLeaveRequest.class)))
                .thenThrow(new VisitNotFoundException(99L));

        mockMvc.perform(put("/api/visits/99/leave")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"purchasedItems\":[]}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Visit not found with ID: 99"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void registerExit_WhenVisitAlreadyClosed_ShouldReturn409() throws Exception {
        when(visitService.registerExit(anyLong(), any(VisitLeaveRequest.class)))
                .thenThrow(new VisitAlreadyClosedException(1L));

        mockMvc.perform(put("/api/visits/1/leave")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"purchasedItems\":[]}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("already closed")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void registerExit_WhenIdIsNegative_ShouldReturn400() throws Exception {
        mockMvc.perform(put("/api/visits/-1/leave")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"purchasedItems\":[]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data.id").value(containsString("positive")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void registerExit_WhenItemsMissing_ShouldReturn400() throws Exception {
        mockMvc.perform(put("/api/visits/1/leave")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data.purchasedItems").exists());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllVisits_ShouldReturn200WithList() throws Exception {
        when(visitService.getAllVisits(any(), any()))
                .thenReturn(List.of(new VisitResponse(1L, "Alice"), new VisitResponse(2L, "Bob")));

        mockMvc.perform(get("/api/visits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Visit list retrieved"))
                .andExpect(jsonPath("$.data", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$.data[0].personName").value("Alice"))
                .andExpect(jsonPath("$.data[1].personName").value("Bob"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllVisits_WithDateRange_ShouldReturn200() throws Exception {
        when(visitService.getAllVisits(any(), any())).thenReturn(List.of(new VisitResponse(1L, "In Range")));

        mockMvc.perform(get("/api/visits")
                        .param("from", "2024-01-01T00:00:00")
                        .param("to", "2024-12-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$.data[0].personName").value("In Range"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllVisits_WhenRangeIsInverted_ShouldReturn400() throws Exception {
        when(visitService.getAllVisits(any(), any()))
                .thenThrow(new InvalidDateRangeException("2024-02-01T00:00:00", "2024-01-01T00:00:00"));

        mockMvc.perform(get("/api/visits")
                        .param("from", "2024-02-01T00:00:00")
                        .param("to", "2024-01-01T00:00:00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getActiveVisits_ShouldReturn200WithList() throws Exception {
        when(visitService.getActiveVisits()).thenReturn(List.of(new VisitResponse(1L, "Inside Customer")));

        mockMvc.perform(get("/api/visits/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Active visit list retrieved"))
                .andExpect(jsonPath("$.data", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$.data[0].personName").value("Inside Customer"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getVisitById_WhenVisitExists_ShouldReturn200() throws Exception {
        when(visitService.getVisitById(1L)).thenReturn(new VisitResponse(1L, "Juan Perez"));

        mockMvc.perform(get("/api/visits/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Visit retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.personName").value("Juan Perez"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getVisitById_WhenVisitNotFound_ShouldReturn404() throws Exception {
        when(visitService.getVisitById(99L)).thenThrow(new VisitNotFoundException(99L));

        mockMvc.perform(get("/api/visits/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Visit not found with ID: 99"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getVisitById_WhenIdIsNotNumeric_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/visits/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("id")));
    }

    @Test
    void getAllVisits_WhenUnauthorized_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/visits")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void registerEntry_WhenBodyIsMalformed_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/visits/enter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not valid json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void registerEntry_WhenContentTypeUnsupported_ShouldReturn415() throws Exception {
        mockMvc.perform(post("/api/visits/enter")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("Maria Gomez"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getUnknownPath_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/does-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllVisits_WhenUnexpectedFailure_ShouldReturn500WithoutLeakingDetails() throws Exception {
        when(visitService.getAllVisits(any(), any()))
                .thenThrow(new IllegalStateException("boom-database-credentials"));

        mockMvc.perform(get("/api/visits"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("Reference:")))
                .andExpect(jsonPath("$.message").value(not(containsString("boom-database-credentials"))));
    }
}
