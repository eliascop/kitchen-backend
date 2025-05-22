package br.com.kitchenbackend.dto;

import java.math.BigDecimal;

public record DebitRequest(BigDecimal amount, String description) {}
