package br.com.kitchen.api.dto;

import java.math.BigDecimal;

public record DebitRequest(BigDecimal amount, String description) {}
