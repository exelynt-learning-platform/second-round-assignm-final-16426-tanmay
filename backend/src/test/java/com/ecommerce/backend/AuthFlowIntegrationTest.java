package com.ecommerce.backend;

import com.ecommerce.backend.entity.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthFlowIntegrationTest {

    @Autowired MockMvc      mockMvc;
    @Autowired ObjectMapper mapper;

    @Test
    void fullAuthFlow_registerThenLoginThenAccessProtected() throws Exception {

        // 1. Register a new user
        User reg = new User();
        reg.setName("Test User");
        reg.setEmail("testuser@example.com");
        reg.setPassword("secret123");
        reg.setRole("USER");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(reg)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("testuser@example.com"));

        // 2. Login with the same credentials → expect a JWT token
        User login = new User();
        login.setEmail("testuser@example.com");
        login.setPassword("secret123");

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andReturn();

        String body  = loginResult.getResponse().getContentAsString();
        JsonNode json = mapper.readTree(body);
        String token  = json.get("token").asText();
        assertThat(token).isNotBlank();

        // 3. Access a protected endpoint with the JWT – should succeed
        mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Widget\",\"description\":\"A widget\",\"price\":9.99,\"stock\":100}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber());

        // 4. Access the same endpoint WITHOUT a token – should be 403
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Gadget\",\"price\":4.99,\"stock\":50}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void login_wrongPassword_returns401() throws Exception {
        // Register first
        User reg = new User();
        reg.setName("Alice");
        reg.setEmail("alice@example.com");
        reg.setPassword("correctPassword");
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(reg)))
            .andExpect(status().isCreated());

        // Login with wrong password
        User bad = new User();
        bad.setEmail("alice@example.com");
        bad.setPassword("wrongPassword");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bad)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid email or password"));
    }

    @Test
    void register_duplicateEmail_returns409() throws Exception {
        User u = new User();
        u.setName("Bob");
        u.setEmail("bob@example.com");
        u.setPassword("pass1234");

        // First registration → 201
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(u)))
                .andExpect(status().isCreated());

        // Second registration with same email → 409 Conflict
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(u)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Email already registered"));
    }
}
