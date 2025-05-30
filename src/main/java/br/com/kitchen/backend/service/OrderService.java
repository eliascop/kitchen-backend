package br.com.kitchen.backend.service;

import br.com.kitchen.backend.dto.OrderDTO;
import br.com.kitchen.backend.model.Order;
import br.com.kitchen.backend.model.User;
import br.com.kitchen.backend.producer.KafkaProducer;
import br.com.kitchen.backend.repository.OrderRepository;
import br.com.kitchen.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService extends GenericService<Order, Long> {

    private final KafkaProducer<OrderDTO> orderProducer;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;

    public OrderService(@Qualifier("orderKafkaProducer") KafkaProducer<OrderDTO> orderProducer,
            OrderRepository repository,
            UserRepository userRepository,
            WalletService walletService) {
        super(repository, Order.class);
        this.orderProducer = orderProducer;
        this.orderRepository = repository;
        this.userRepository = userRepository;
        this.walletService = walletService;
    }

    @Transactional
    public Order createOrder(Order order) {
        if (order.getUser() != null && order.getUser().getId() != null) {
            User user = userRepository.findById(order.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            order.setUser(user);
        }else{
            throw new IllegalArgumentException("User must be set for the order");
        }
        walletService.debit(order.getUser().getId(), order.getTotal(), "COMPRA");
        Order orderSaved = orderRepository.save(order);
        orderProducer.sendNotification(new OrderDTO(orderSaved.getId(), orderSaved.getStatus()));
        return orderSaved;
    }

    public Optional<List<Order>> findOrdersByUserId(Long userId) {
        if(userId == 1){
            return Optional.of(orderRepository.findAll());
        }else {
            return orderRepository.findOrdersByUserId(userId);
        }
    }

    public Optional<Order> findOrderByIdAndUserId(Long id, Long userId) {
        if(userId == 1){
            return orderRepository.findById(id);
        }else {
            return orderRepository.findOrderByIdAndUserId(id, userId);
        }
     }

}
