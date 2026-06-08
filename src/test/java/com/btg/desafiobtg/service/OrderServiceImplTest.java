package com.btg.desafiobtg.service;

import com.btg.desafiobtg.domain.enums.OrderStatus;
import com.btg.desafiobtg.domain.model.Order;
import com.btg.desafiobtg.dto.CreateOrderRequest;
import com.btg.desafiobtg.dto.UpdateOrderStatusRequest;
import com.btg.desafiobtg.exception.OrderNotFoundException;
import com.btg.desafiobtg.repository.OrderRepository;
import com.btg.desafiobtg.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository);
    }

    @Test
    void createOrder_shouldPersistAndReturnPendingOrder() {
        var request = new CreateOrderRequest("Alice", new BigDecimal("250.00"));
        var saved = buildOrder(1L, "Alice", new BigDecimal("250.00"), OrderStatus.PENDING);
        when(orderRepository.save(any(Order.class))).thenReturn(saved);

        var response = orderService.createOrder(request);

        assertThat(response.customerName()).isEqualTo("Alice");
        assertThat(response.status()).isEqualTo(OrderStatus.PENDING);
        assertThat(response.totalAmount()).isEqualByComparingTo("250.00");
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void listOrders_withStatus_shouldFilterByStatus() {
        var order = buildOrder(1L, "Bob", BigDecimal.TEN, OrderStatus.COMPLETED);
        when(orderRepository.findByStatus(OrderStatus.COMPLETED)).thenReturn(List.of(order));

        var result = orderService.listOrders(OrderStatus.COMPLETED);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().status()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    void listOrders_withoutStatus_shouldReturnAll() {
        when(orderRepository.findAll()).thenReturn(List.of(
            buildOrder(1L, "Alice", BigDecimal.TEN, OrderStatus.PENDING),
            buildOrder(2L, "Bob", BigDecimal.ONE, OrderStatus.COMPLETED)
        ));

        var result = orderService.listOrders(null);

        assertThat(result).hasSize(2);
    }

    @Test
    void findOrderById_shouldReturnOrder() {
        var order = buildOrder(1L, "Carol", BigDecimal.TEN, OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        var response = orderService.findOrderById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.customerName()).isEqualTo("Carol");
    }

    @Test
    void findOrderById_shouldThrowWhenNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.findOrderById(99L))
            .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void updateOrderStatus_shouldChangeStatus() {
        var order = buildOrder(1L, "Dave", BigDecimal.TEN, OrderStatus.PENDING);
        var updated = buildOrder(1L, "Dave", BigDecimal.TEN, OrderStatus.PROCESSING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(updated);

        var response = orderService.updateOrderStatus(1L, new UpdateOrderStatusRequest(OrderStatus.PROCESSING));

        assertThat(response.status()).isEqualTo(OrderStatus.PROCESSING);
    }

    @Test
    void deleteOrder_shouldCallDeleteById() {
        when(orderRepository.existsById(1L)).thenReturn(true);

        orderService.deleteOrder(1L);

        verify(orderRepository).deleteById(1L);
    }

    @Test
    void deleteOrder_shouldThrowWhenNotFound() {
        when(orderRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> orderService.deleteOrder(99L))
            .isInstanceOf(OrderNotFoundException.class);
    }

    private Order buildOrder(Long id, String customerName, BigDecimal amount, OrderStatus status) {
        return Order.builder()
            .id(id)
            .customerName(customerName)
            .totalAmount(amount)
            .status(status)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
}