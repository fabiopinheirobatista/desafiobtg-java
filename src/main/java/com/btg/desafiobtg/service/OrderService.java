package com.btg.desafiobtg.service;

import com.btg.desafiobtg.domain.enums.OrderStatus;
import com.btg.desafiobtg.dto.CreateOrderRequest;
import com.btg.desafiobtg.dto.OrderResponse;
import com.btg.desafiobtg.dto.UpdateOrderStatusRequest;
import java.util.List;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);
    List<OrderResponse> listOrders(OrderStatus status);
    OrderResponse findOrderById(Long id);
    OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request);
    void deleteOrder(Long id);
}