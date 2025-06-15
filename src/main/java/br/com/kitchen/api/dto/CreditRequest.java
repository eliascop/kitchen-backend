package br.com.kitchen.api.dto;

import java.math.BigDecimal;

public record CreditRequest(BigDecimal amount, String description) {}
