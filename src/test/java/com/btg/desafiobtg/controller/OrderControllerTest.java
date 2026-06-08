package com.btg.desafiobtg.controller;

import com.btg.desafiobtg.domain.enums.OrderStatus;
import com.btg.desafiobtg.dto.CreateOrderRequest;
import com.btg.desafiobtg.dto.UpdateOrderStatusRequest;
import com.btg.desafiobtg.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class OrderControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;

    OrderControllerTest(MockMvc mockMvc, ObjectMapper objectMapper, OrderRepository orderRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.orderRepository = orderRepository;
    }

    @BeforeEach
    void cleanUp() {
        orderRepository.deleteAll();
    }

    @Test
    void createOrder_shouldReturn201WithBody() throws Exception {
        var request = new CreateOrderRequest("John Doe", new BigDecimal("150.00"));

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.customerName").value("John Doe"))
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void createOrder_shouldReturn422WhenCustomerNameIsBlank() throws Exception {
        var request = new CreateOrderRequest("", new BigDecimal("150.00"));

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.title").value("Validation Error"));
    }

    @Test
    void listOrders_shouldReturnAllOrders() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new CreateOrderRequest("Alice", new BigDecimal("100.00")))));

        mockMvc.perform(get("/api/v1/orders"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void listOrders_withStatusFilter_shouldReturnFilteredOrders() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new CreateOrderRequest("Bob", new BigDecimal("200.00")))));

        mockMvc.perform(get("/api/v1/orders").param("status", "PENDING"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(get("/api/v1/orders").param("status", "COMPLETED"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void findOrderById_shouldReturn200() throws Exception {
        var createResult = mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateOrderRequest("Carol", new BigDecimal("300.00")))))
            .andReturn();

        var id = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/api/v1/orders/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerName").value("Carol"));
    }

    @Test
    void findOrderById_shouldReturn404WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/orders/9999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateOrderStatus_shouldReturn200WithNewStatus() throws Exception {
        var createResult = mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateOrderRequest("Dave", new BigDecimal("50.00")))))
            .andReturn();

        var id = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(patch("/api/v1/orders/{id}/status", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new UpdateOrderStatusRequest(OrderStatus.PROCESSING))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PROCESSING"));
    }

    @Test
    void deleteOrder_shouldReturn204AndThenReturn404() throws Exception {
        var createResult = mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateOrderRequest("Eve", new BigDecimal("75.00")))))
            .andReturn();

        var id = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/api/v1/orders/{id}", id))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/orders/{id}", id))
            .andExpect(status().isNotFound());
    }
}