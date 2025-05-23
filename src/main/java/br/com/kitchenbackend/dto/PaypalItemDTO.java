package br.com.kitchenbackend.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaypalItemDTO {
    private String name;
    private String description;
    private String quantity;
    private UnitAmountDTO unit_amount;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UnitAmountDTO {
        private String currency_code;
        private String value;
    }
}
