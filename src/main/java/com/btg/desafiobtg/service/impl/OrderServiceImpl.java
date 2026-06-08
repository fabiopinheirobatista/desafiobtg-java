package com.btg.desafiobtg.service.impl;

import com.btg.desafiobtg.domain.enums.OrderStatus;
import com.btg.desafiobtg.domain.model.Order;
import com.btg.desafiobtg.dto.CreateOrderRequest;
import com.btg.desafiobtg.dto.OrderResponse;
import com.btg.desafiobtg.dto.UpdateOrderStatusRequest;
import com.btg.desafiobtg.exception.OrderNotFoundException;
import com.btg.desafiobtg.repository.OrderRepository;
import com.btg.desafiobtg.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        var order = Order.builder()
            .customerName(request.customerName())
            .totalAmount(request.totalAmount())
            .status(OrderStatus.PENDING)
            .build();
        return toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> listOrders(OrderStatus status) {
        var orders = status != null
            ? orderRepository.findByStatus(status)
            : orderRepository.findAll();
        return orders.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse findOrderById(Long id) {
        return orderRepository.findById(id)
            .map(this::toResponse)
            .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request) {
        var order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));
        order.setStatus(request.status());
        return toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException(id);
        }
        orderRepository.deleteById(id);
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
            order.getId(),
            order.getCustomerName(),
            order.getTotalAmount(),
            order.getStatus(),
            order.getCreatedAt(),
            order.getUpdatedAt()
        );
    }
}