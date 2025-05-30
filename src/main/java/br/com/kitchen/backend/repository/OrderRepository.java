package br.com.kitchen.backend.repository;

import br.com.kitchen.backend.model.Order;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends GenericRepository<Order, Long> {
    Optional<List<Order>> findOrdersByUserId(Long userId);
    Optional<Order> findOrderByIdAndUserId(Long id, Long userId);
}

