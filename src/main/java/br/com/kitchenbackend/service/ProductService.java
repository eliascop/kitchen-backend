package br.com.kitchenbackend.service;

import br.com.kitchenbackend.model.Product;
import br.com.kitchenbackend.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductService extends GenericService<Product, Long> {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository repository) {
        super(repository, Product.class);
        this.productRepository = repository;
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Product product){
        this.productRepository.delete(product);
    }
}
