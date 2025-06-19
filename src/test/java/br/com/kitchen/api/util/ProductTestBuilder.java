package br.com.kitchen.api.util;

import br.com.kitchen.api.model.Product;

import java.math.BigDecimal;

public class ProductTestBuilder {

    public static Product buildDefaultProduct() {
        return new Product(null, "Pizza", "Delicious thin crust pizza", "MEAL", BigDecimal.valueOf(100));
    }

    public static Product buildWithCustomValues(String name, BigDecimal price) {
        Product product = new Product();
        product.setName(name);
        product.setDescription("Description for " + name);
        product.setType("MEAL");
        product.setPrice(price);
        return product;
    }
}
