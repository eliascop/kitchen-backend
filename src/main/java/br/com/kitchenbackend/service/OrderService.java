package br.com.kitchenbackend.service;

import br.com.kitchenbackend.model.Order;
import br.com.kitchenbackend.model.User;
import br.com.kitchenbackend.repository.OrderRepository;
import br.com.kitchenbackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService extends GenericService<Order, Long> {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository repository,
                        UserRepository userRepository) {
        super(repository, Order.class);
        this.orderRepository = repository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Order save(Order order) {
        if (order.getUser() != null && order.getUser().getId() != null) {
            User user = userRepository.findById(order.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            order.setUser(user);
        }

        return orderRepository.save(order);
    }

    public Optional<List<Order>> findOrdersByUserId(Long userId) {
        return orderRepository.findOrdersByUserId(userId);
    }

    public Optional<Order> findOrderByIdAndUserId(Long id, Long userId) {
        return orderRepository.findOrderByIdAndUserId(id, userId);
    }

}
