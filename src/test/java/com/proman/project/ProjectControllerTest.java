package com.proman.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proman.auth.dto.AuthResponse;
import com.proman.auth.dto.RegisterRequest;
import com.proman.project.dto.CreateProjectRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProjectControllerTest {

    @Autowired private WebApplicationContext context;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private MockMvc mockMvc;
    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .build();

        var register = new RegisterRequest("proj-test@example.com", "password123", "Test User");
        String response = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)))
            .andReturn().getResponse().getContentAsString();
        accessToken = objectMapper.readValue(response, AuthResponse.class).accessToken();
    }

    @Test
    void createAndGetProject() throws Exception {
        var request = new CreateProjectRequest("PROJ", "My Project", "Description");

        String response = mockMvc.perform(post("/api/projects")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.key").value("PROJ"))
            .andReturn().getResponse().getContentAsString();

        String projectId = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(get("/api/projects/" + projectId)
                .header("Authorization", "Bearer " + accessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("My Project"));
    }

    @Test
    void listProjectsReturnsUserProjects() throws Exception {
        var request = new CreateProjectRequest("TST", "Test", null);
        mockMvc.perform(post("/api/projects")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/api/projects")
                .header("Authorization", "Bearer " + accessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }
}
