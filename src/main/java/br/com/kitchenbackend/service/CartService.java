package br.com.kitchenbackend.service;

import br.com.kitchenbackend.model.Cart;
import br.com.kitchenbackend.model.CartItems;
import br.com.kitchenbackend.model.Product;
import br.com.kitchenbackend.repository.CartRepository;
import br.com.kitchenbackend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class CartService extends GenericService<Cart, Long> {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        super(cartRepository, Cart.class);
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public Cart save(Cart cart) {
        if (cart.getItems() != null && !cart.getItems().isEmpty()) {
            for (CartItems item : cart.getItems()) {
                item.setCart(cart);
                Product resolvedProduct = productRepository.findById(item.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Produto não encontrado: ID " + item.getProduct().getId()));
                item.setProduct(resolvedProduct);
                item.calculateItemValue();
            }
        }

        BigDecimal total = cart.getItems().stream()
                .map(CartItems::getValue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotal(total);
        return cartRepository.save(cart);
    }
}
