package com.btg.desafiobtg.repository;

import com.btg.desafiobtg.domain.enums.OrderStatus;
import com.btg.desafiobtg.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);
}