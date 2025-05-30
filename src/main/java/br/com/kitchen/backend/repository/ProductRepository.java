package br.com.kitchen.backend.repository;

import br.com.kitchen.backend.model.Product;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends GenericRepository<Product, Long> {}