package br.com.kitchen.backend.dto;

import java.math.BigDecimal;

public record CreditRequest(BigDecimal amount, String description) {}
