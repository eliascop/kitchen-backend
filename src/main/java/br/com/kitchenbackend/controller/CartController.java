package br.com.kitchenbackend.controller;

import br.com.kitchenbackend.model.Cart;
import br.com.kitchenbackend.dto.OrderDTO;
import br.com.kitchenbackend.model.Order;
import br.com.kitchenbackend.producer.KafkaProducer;
import br.com.kitchenbackend.service.CartService;
import br.com.kitchenbackend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/carts/v1")
public class CartController {

    private final KafkaProducer<OrderDTO> orderProducer;
    private final CartService cartService;
    private final OrderService orderService;

    @Autowired
    public CartController(@Qualifier("orderKafkaProducer") KafkaProducer<OrderDTO> orderProducer,
                          CartService cartService,
                          OrderService orderService) {
        this.orderProducer = orderProducer;
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> createCart(@RequestBody Cart cart) {
        try {
            Cart savedCart = cartService.save(cart);
            Order newOrder = orderService.createOrderFromCart(savedCart.getId(), savedCart.getUser().getId());
            orderProducer.sendNotification(new OrderDTO(newOrder.getId(), newOrder.getStatus()));

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Order created",
                            "orderId", newOrder.getId()
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

    @GetMapping("/{id}")
    public ResponseEntity<Cart> getCartById(@PathVariable("id") Long id) {
        Optional<Cart> cart = Optional.ofNullable(cartService.findById(id));
        return cart.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}