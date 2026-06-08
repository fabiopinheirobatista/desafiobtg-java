package com.btg.desafiobtg.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateOrderRequest(
    @NotBlank String customerName,
    @NotNull @DecimalMin("0.01") BigDecimal totalAmount
) {}