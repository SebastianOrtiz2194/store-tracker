package com.store.tracker.visits.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.tracker.visits.dto.VisitEntryRequest;
import com.store.tracker.visits.dto.VisitLeaveRequest;
import com.store.tracker.visits.dto.VisitResponse;
import com.store.tracker.exception.VisitNotFoundException;
import com.store.tracker.visits.service.VisitService;
import com.store.tracker.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VisitController.class)
@Import(SecurityConfig.class)
public class VisitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VisitService visitService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void registerEntry_WhenValidRequest_ShouldReturn200() throws Exception {
        // given
        VisitEntryRequest request = new VisitEntryRequest("Maria Gomez");
        VisitResponse response = new VisitResponse(1L, "Maria Gomez");
        when(visitService.registerEntry(any(VisitEntryRequest.class))).thenReturn(response);

        // when / then
        mockMvc.perform(post("/api/visits/enter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.personName").value("Maria Gomez"))
                .andExpect(jsonPath("$.message").value("Entry registered successfully"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void registerEntry_WhenInvalidRequest_ShouldReturn400() throws Exception {
        // given
        VisitEntryRequest request = new VisitEntryRequest("");

        // when / then
        mockMvc.perform(post("/api/visits/enter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void registerEntry_WhenUnauthorized_ShouldReturn401() throws Exception {
        VisitEntryRequest request = new VisitEntryRequest("Unauthenticated User");

        mockMvc.perform(post("/api/visits/enter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void registerExit_WhenValidRequest_ShouldReturn200() throws Exception {
        // given
        VisitLeaveRequest request = new VisitLeaveRequest(List.of(), 150.0);
        VisitResponse response = new VisitResponse(
                1L, "Juan Perez", LocalDateTime.now().minusHours(1),
                LocalDateTime.now(), List.of(), 150.0);
        when(visitService.registerExit(anyLong(), any(VisitLeaveRequest.class))).thenReturn(response);

        // when / then
        mockMvc.perform(put("/api/visits/1/leave")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Exit registered successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.totalSpent").value(150.0));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void registerExit_WhenVisitNotFound_ShouldReturn404() throws Exception {
        // given
        VisitLeaveRequest request = new VisitLeaveRequest(List.of(), 0.0);
        when(visitService.registerExit(anyLong(), any(VisitLeaveRequest.class)))
                .thenThrow(new VisitNotFoundException("Visit not found with ID: 99"));

        // when / then
        mockMvc.perform(put("/api/visits/99/leave")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Visit not found with ID: 99"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllVisits_ShouldReturn200WithList() throws Exception {
        // given
        VisitResponse first = new VisitResponse(1L, "Alice");
        VisitResponse second = new VisitResponse(2L, "Bob");
        when(visitService.getAllVisits(any(), any())).thenReturn(List.of(first, second));

        // when / then
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
    void getAllVisits_WithDateRange_ShouldReturn200WithFilteredList() throws Exception {
        // given
        VisitResponse filtered = new VisitResponse(1L, "In Range");
        when(visitService.getAllVisits(any(), any())).thenReturn(List.of(filtered));

        // when / then
        mockMvc.perform(get("/api/visits")
                        .param("from", "2024-01-01T00:00:00")
                        .param("to", "2024-12-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Visit list retrieved"))
                .andExpect(jsonPath("$.data", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$.data[0].personName").value("In Range"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getActiveVisits_ShouldReturn200WithList() throws Exception {
        // given
        VisitResponse active = new VisitResponse(1L, "Inside Customer");
        when(visitService.getActiveVisits()).thenReturn(List.of(active));

        // when / then
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
        // given
        VisitResponse response = new VisitResponse(1L, "Juan Perez");
        when(visitService.getVisitById(1L)).thenReturn(response);

        // when / then
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
        // given
        when(visitService.getVisitById(99L))
                .thenThrow(new VisitNotFoundException("Visit not found with ID: 99"));

        // when / then
        mockMvc.perform(get("/api/visits/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Visit not found with ID: 99"));
    }

    @Test
    void getAllVisits_WhenUnauthorized_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/visits"))
                .andExpect(status().isUnauthorized());
    }
}
