package br.com.kitchen.backend.dto;

import java.math.BigDecimal;

public record DebitRequest(BigDecimal amount, String description) {}
