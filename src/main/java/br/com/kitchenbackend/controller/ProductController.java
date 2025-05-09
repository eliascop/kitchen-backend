package br.com.kitchenbackend.controller;

import br.com.kitchenbackend.model.ModelNotification;
import br.com.kitchenbackend.model.Product;
import br.com.kitchenbackend.producer.KafkaProducer;
import br.com.kitchenbackend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products/v1")
public class ProductController {

    private final KafkaProducer<ModelNotification> productProducer;
    private final ProductService service;

    @Autowired
    public ProductController(@Qualifier("productKafkaProducer") KafkaProducer<ModelNotification> productProducer,
                             ProductService service) {
        this.productProducer = productProducer;
        this.service = service;
    }

    @GetMapping
    public List<Product> showAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/search")
    public List<Product> findByType(@RequestParam String type) {
        return service.findByField("type", type);
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        try{
            Product productSaved = service.save(product);
            productProducer.sendNotification(new ModelNotification(productSaved.getId()));

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Product created",
                            "productId", productSaved.getId()
                    ));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "errorCode", 500,
                            "message", "An error occurred when saving order",
                            "details", e.getMessage()
                    ));
        }
    }
}