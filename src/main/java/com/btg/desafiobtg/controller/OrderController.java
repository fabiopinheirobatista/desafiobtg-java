package com.btg.desafiobtg.controller;

import com.btg.desafiobtg.domain.enums.OrderStatus;
import com.btg.desafiobtg.dto.CreateOrderRequest;
import com.btg.desafiobtg.dto.OrderResponse;
import com.btg.desafiobtg.dto.UpdateOrderStatusRequest;
import com.btg.desafiobtg.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> listOrders(
        @RequestParam(required = false) OrderStatus status
    ) {
        return ResponseEntity.ok(orderService.listOrders(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findOrderById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
        @PathVariable Long id,
        @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}