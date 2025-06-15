package br.com.kitchen.api.controller;

import br.com.kitchen.api.dto.ProductDTO;
import br.com.kitchen.api.model.Product;
import br.com.kitchen.api.producer.KafkaProducer;
import br.com.kitchen.api.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/products/v1")
public class ProductController {

    private final KafkaProducer<ProductDTO> productProducer;
    private final ProductService service;

    @Autowired
    public ProductController(@Qualifier("productKafkaProducer") KafkaProducer<ProductDTO> productProducer,
                             ProductService service) {
        this.productProducer = productProducer;
        this.service = service;
    }

    @GetMapping
    public List<Product> showAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Product> getProductById(@PathVariable Long id) {
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
            productProducer.sendNotification(new ProductDTO(productSaved.getId()));

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
                            "message", "An error occurred when saving product",
                            "details", e.getMessage()
                    ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try{
            Optional<Product> product = service.findById(id);
            product.ifPresent(p ->{
                service.deleteProduct(id);
            });
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Product deleted successfully",
                            "code", HttpStatus.OK
                    ));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "errorCode", 500,
                            "message", "An error occurred when delete product",
                            "details", e.getMessage()
                    ));
        }

    }
}