package br.com.kitchen.backend.controller;

import br.com.kitchen.backend.model.Order;
import br.com.kitchen.backend.security.CustomUserDetails;
import br.com.kitchen.backend.service.OrderService;
import br.com.kitchen.backend.util.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/orders/v1")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService,
                            JwtTokenProvider jwtTokenProvider) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<Order>> showAll() {
        List<Order> orders = orderService.findAll();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        return orderService.findOrderByIdAndUserId(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Order>> findOrdersByUserId(@RequestParam Long userId) {
        if (userId == null || userId == 0) {
            return ResponseEntity.badRequest().build();
        }

        return orderService.findOrdersByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping("/create")
    public ResponseEntity<?> newOrder(@RequestBody Order order,
                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        try{
            Order orderSaved = orderService.createOrder(order);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "code", 200,
                            "message", "Order successfully created",
                            "orderId", orderSaved.getId()
                    ));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "code", 402,
                            "message", "An error occurred while creating order",
                            "details", e.getMessage()
                    ));
        }
    }

}
