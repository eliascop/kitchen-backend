package br.com.kitchenbackend.controller;

import br.com.kitchenbackend.model.Order;
import br.com.kitchenbackend.service.OrderService;
import br.com.kitchenbackend.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders/v1")
public class OrderController {

    private final JwtTokenProvider jwtTokenProvider;
    private final OrderService service;

    @Autowired
    public OrderController(OrderService service, JwtTokenProvider jwtTokenProvider) {
        this.service = service;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping
    public ResponseEntity<List<Order>> showAll() {
        List<Order> orders = service.findAll();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id, HttpServletRequest request) {
        String token = jwtTokenProvider.getTokenFromRequest(request);
        if (token != null) {
            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            Optional<Order> order = service.findOrderByIdAndUserId(id,userId);
            return order.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Order>> findOrdersByUserId(@RequestParam Long userId) {
        if (userId == null || userId == 0) {
            return ResponseEntity.badRequest().build();
        }

        Optional<List<Order>> orders = service.findOrdersByUserId(userId);
        return orders.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());

    }

}
