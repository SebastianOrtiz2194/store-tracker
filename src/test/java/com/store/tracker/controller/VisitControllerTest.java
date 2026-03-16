package com.store.tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.tracker.dto.VisitEntryRequest;
import com.store.tracker.dto.VisitResponse;
import com.store.tracker.service.VisitService;
import com.store.tracker.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        // Arrange
        VisitEntryRequest request = new VisitEntryRequest();
        request.setPersonName("Maria Gomez");
        
        VisitResponse response = new VisitResponse();
        response.setId(1L);
        response.setPersonName("Maria Gomez");

        when(visitService.registerEntry(any(VisitEntryRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/visits/enter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.personName").value("Maria Gomez"))
                .andExpect(jsonPath("$.message").value("Entrada registrada con éxito"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void registerEntry_WhenInvalidRequest_ShouldReturn400() throws Exception {
        // Arrange
        VisitEntryRequest request = new VisitEntryRequest();
        request.setPersonName(""); // Name is blank, should trigger validation error

        // Act & Assert
        mockMvc.perform(post("/api/visits/enter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void registerEntry_WhenUnauthorized_ShouldReturn401() throws Exception {
        VisitEntryRequest request = new VisitEntryRequest();
        request.setPersonName("Unauthenticated User");

        mockMvc.perform(post("/api/visits/enter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
