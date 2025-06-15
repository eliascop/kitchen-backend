package br.com.kitchen.api.repository;

import br.com.kitchen.api.model.Product;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends GenericRepository<Product, Long> {}