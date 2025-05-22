package br.com.kitchenbackend.dto;

import java.math.BigDecimal;

public record CreditRequest(BigDecimal amount, String description) {}
