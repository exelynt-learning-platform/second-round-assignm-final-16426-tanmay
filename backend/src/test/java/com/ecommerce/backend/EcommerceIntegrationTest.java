package com.ecommerce.backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EcommerceIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper mapper;

    private String register(String name, String email, String password, String role) throws Exception {
        String body = String.format(
            "{\"name\":\"%s\",\"email\":\"%s\",\"password\":\"%s\",\"role\":\"%s\"}",
            name, email, password, role);
        MvcResult res = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn();
        return mapper.readTree(res.getResponse().getContentAsString())
                .get("data").get("token").asText();
    }

    private Long createProduct(String adminToken, String name, double price, int stock) throws Exception {
        String body = String.format(
            "{\"name\":\"%s\",\"description\":\"Test\",\"price\":%.2f,\"stock\":%d}", name, price, stock);
        MvcResult res = mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn();
        return mapper.readTree(res.getResponse().getContentAsString())
                .get("data").get("id").asLong();
    }

    // ── Auth tests ────────────────────────────────────────────────────────────
    @Test @Order(1)
    void register_success() throws Exception {
        register("Alice", "alice@test.com", "Password1!", "USER");
    }

    @Test @Order(2)
    void register_weakPassword_returns422() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Bob\",\"email\":\"bob@test.com\",\"password\":\"weak\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.password").exists());
    }

    @Test @Order(3)
    void register_duplicateEmail_returns400() throws Exception {
        register("Alice", "dup@test.com", "Password1!", "USER");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Alice2\",\"email\":\"dup@test.com\",\"password\":\"Password1!\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test @Order(4)
    void login_success_returnsToken() throws Exception {
        register("Carol", "carol@test.com", "Password1!", "USER");
        MvcResult res = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"carol@test.com\",\"password\":\"Password1!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").isString())
                .andReturn();
        String token = mapper.readTree(res.getResponse().getContentAsString())
                .get("data").get("token").asText();
        Assertions.assertFalse(token.isBlank());
    }

    @Test @Order(5)
    void login_wrongPassword_returns400() throws Exception {
        register("Dave", "dave@test.com", "Password1!", "USER");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"dave@test.com\",\"password\":\"WrongPass1!\"}"))
                .andExpect(status().isBadRequest());
    }

    // ── Product tests ─────────────────────────────────────────────────────────
    @Test @Order(6)
    void getProducts_publicNoToken() throws Exception {
        mockMvc.perform(get("/api/products")).andExpect(status().isOk());
    }

    @Test @Order(7)
    void createProduct_asAdmin_success() throws Exception {
        String admin = register("Admin", "admin@test.com", "Admin123!", "ADMIN");
        Long id = createProduct(admin, "Laptop", 999.99, 10);
        Assertions.assertTrue(id > 0);
    }

    @Test @Order(8)
    void createProduct_asUser_returns403() throws Exception {
        String user = register("User1", "user1@test.com", "User1234!", "USER");
        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + user)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Phone\",\"price\":499.99,\"stock\":5}"))
                .andExpect(status().isForbidden());
    }

    @Test @Order(9)
    void createProduct_negativePrice_returns422() throws Exception {
        String admin = register("Admin2", "admin2@test.com", "Admin123!", "ADMIN");
        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + admin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"BadProduct\",\"price\":-1.0,\"stock\":5}"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test @Order(10)
    void createProduct_negativeStock_returns422() throws Exception {
        String admin = register("Admin3", "admin3@test.com", "Admin123!", "ADMIN");
        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + admin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"BadStock\",\"price\":10.0,\"stock\":-1}"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test @Order(11)
    void createProduct_duplicateName_returns400() throws Exception {
        String admin = register("Admin4", "admin4@test.com", "Admin123!", "ADMIN");
        createProduct(admin, "UniqueItem", 10.0, 5);
        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + admin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"UniqueItem\",\"price\":20.0,\"stock\":3}"))
                .andExpect(status().isBadRequest());
    }

    // ── Full e-commerce flow: cart → order → payment ──────────────────────────
    @Test @Order(12)
    void fullPurchaseFlow() throws Exception {
        String admin = register("Admin5", "admin5@test.com", "Admin123!", "ADMIN");
        String user  = register("Buyer",  "buyer@test.com",  "Buyer123!", "USER");
        Long productId = createProduct(admin, "Headphones", 79.99, 20);

        // 1. Add item to cart
        mockMvc.perform(post("/api/cart/items")
                .header("Authorization", "Bearer " + user)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":" + productId + ",\"quantity\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items", hasSize(1)));

        // 2. View cart total
        mockMvc.perform(get("/api/cart")
                .header("Authorization", "Bearer " + user))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalAmount").value(closeTo(159.98, 0.01)));

        // 3. Place order
        MvcResult orderRes = mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + user)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"shippingAddress\":\"123 Main St, Mumbai\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"))
                .andReturn();

        Long orderId = mapper.readTree(orderRes.getResponse().getContentAsString())
                .get("data").get("id").asLong();

        // 4. Pay for order
        mockMvc.perform(post("/api/payments")
                .header("Authorization", "Bearer " + user)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"orderId\":" + orderId + ",\"paymentMethod\":\"CREDIT_CARD\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"))
                .andExpect(jsonPath("$.data.transactionId", startsWith("TXN-")));

        // 5. Verify order status updated to PROCESSING
        mockMvc.perform(get("/api/orders/" + orderId)
                .header("Authorization", "Bearer " + user))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PROCESSING"));

        // 6. Admin updates order to SHIPPED
        mockMvc.perform(patch("/api/orders/" + orderId + "/status")
                .header("Authorization", "Bearer " + admin)
                .param("status", "SHIPPED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("SHIPPED"));
    }
}
