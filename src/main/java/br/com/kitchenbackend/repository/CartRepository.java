package br.com.kitchenbackend.repository;

import br.com.kitchenbackend.model.Cart;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends GenericRepository<Cart, Long> {}