package br.com.kitchenbackend.service;

import br.com.kitchenbackend.model.*;
import br.com.kitchenbackend.repository.CartRepository;
import br.com.kitchenbackend.repository.OrderRepository;
import br.com.kitchenbackend.repository.ProductRepository;
import br.com.kitchenbackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService extends GenericService<Order, Long> {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(CartRepository cartRepository,
                        OrderRepository repository,
                        ProductRepository productRepository,
                        UserRepository userRepository) {
        super(repository, Order.class);
        this.cartRepository = cartRepository;
        this.orderRepository = repository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Order save(Order order) {
        order.setStatus("PENDING");
        order.setCreation(new Date());

        if (order.getUser() != null && order.getUser().getId() != null) {
            User user = userRepository.findById(order.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            order.setUser(user);
        }

        Cart cart = order.getCart();
        if (cart != null && cart.getItems() != null) {
            for (CartItems item : cart.getItems()) {
                item.setCart(cart);
                Product resolvedProduct = productRepository.findById(item.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Produto não encontrado: ID " + item.getProduct().getId()));
                item.setProduct(resolvedProduct);
                item.calculateItemValue();
            }
        }

        return orderRepository.save(order);
    }

    @Transactional
    public Order createOrderFromCart(Long cartId, Long userId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Order order = new Order();
        order.setCart(cart);
        order.setUser(user);
        order.setStatus("PENDING");
        order.setCreation(new Date());

        return orderRepository.save(order);
    }

    public Optional<List<Order>> findOrdersByUserId(Long userId) {
        return orderRepository.findOrdersByUserId(userId);
    }

    public Optional<Order> findOrderByIdAndUserId(Long id, Long userId) {
        return orderRepository.findOrderByIdAndUserId(id, userId);
    }

}
