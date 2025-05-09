package br.com.kitchenbackend.repository;

import br.com.kitchenbackend.model.Product;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends GenericRepository<Product, Long> {}