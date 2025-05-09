package br.com.kitchenbackend.repository;

import br.com.kitchenbackend.model.Order;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends GenericRepository<Order, Long> {
    Optional<List<Order>> findOrdersByUserId(Long userId);
    Optional<Order> findOrderByIdAndUserId(Long id, Long userId);
}

