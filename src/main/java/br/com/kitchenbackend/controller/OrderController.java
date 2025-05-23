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
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/orders/v1")
public class OrderController {

    private final JwtTokenProvider jwtTokenProvider;
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService,
                            JwtTokenProvider jwtTokenProvider) {
        this.orderService = orderService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping
    public ResponseEntity<List<Order>> showAll() {
        List<Order> orders = orderService.findAll();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id, HttpServletRequest request) {
        String token = jwtTokenProvider.getTokenFromRequest(request);
        if (token != null) {
            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            Optional<Order> order;
            if(userId == 1){
                order = Optional.ofNullable(orderService.findById(id));
            }else {
                order = orderService.findOrderByIdAndUserId(id, userId);
            }
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

        Optional<List<Order>> orders;
        if(userId == 1){
            orders = Optional.ofNullable(orderService.findAll());
        }else {
            orders = orderService.findOrdersByUserId(userId);
        }

        return orders.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());

    }

    @PostMapping("/create")
    public ResponseEntity<?> newOrder(@RequestBody Order order, HttpServletRequest request) {
        try{
            String token = jwtTokenProvider.getTokenFromRequest(request);
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
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
