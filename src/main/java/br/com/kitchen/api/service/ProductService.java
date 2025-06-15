package br.com.kitchen.api.service;

import br.com.kitchen.api.model.Product;
import br.com.kitchen.api.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService extends GenericService<Product, Long> {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository repository) {
        super(repository, Product.class);
        this.productRepository = repository;
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id){
        this.productRepository.deleteById(id);
    }
}
