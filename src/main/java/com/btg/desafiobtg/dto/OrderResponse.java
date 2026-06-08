package com.btg.desafiobtg.dto;

import com.btg.desafiobtg.domain.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
    Long id,
    String customerName,
    BigDecimal totalAmount,
    OrderStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}