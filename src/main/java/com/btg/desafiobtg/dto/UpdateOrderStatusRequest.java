package com.btg.desafiobtg.dto;

import com.btg.desafiobtg.domain.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
    @NotNull OrderStatus status
) {}