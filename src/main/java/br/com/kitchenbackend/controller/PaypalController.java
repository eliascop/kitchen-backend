package br.com.kitchenbackend.controller;

import br.com.kitchenbackend.dto.OrderDTO;
import br.com.kitchenbackend.model.Order;
import br.com.kitchenbackend.producer.KafkaProducer;
import br.com.kitchenbackend.service.OrderService;
import br.com.kitchenbackend.service.PaypalService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/paypal")
public class PaypalController {

    private final PaypalService paypalService;
    private final KafkaProducer<OrderDTO> orderProducer;
    private final OrderService orderService;

    @Value("${frontend.base.url}")
    private String urlHome;

    @Autowired
    public PaypalController(@Qualifier("orderKafkaProducer") KafkaProducer<OrderDTO> orderProducer,
                            OrderService orderService,
                            PaypalService paypalService) {
        this.orderProducer = orderProducer;
        this.orderService = orderService;
        this.paypalService = paypalService;
    }

    @PostMapping("/payment")
    public ResponseEntity<?> pay(@RequestBody Order order) {
        try {
            Order savedOrder = orderService.save(order);
            String approvalLink = paypalService.doPayment(savedOrder);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Map.of(
                            "redirect", approvalLink
                    ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ocorreu um erro ao iniciar pagamento: " + e.getMessage());
        }
    }

    @GetMapping("/success")
    public void success(@RequestParam("token") String token,
                        @RequestParam("orderId") Long orderId,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        String redirect = urlHome;
        try {
            String status = paypalService.confirmPayment(token);
            Order orderPaid = orderService.findById(orderId);
            if ("SUCCESS".equals(status) || "COMPLETED".equals(status)) {
                orderPaid.setStatus("PENDING");
                orderPaid.setPaymentId(request.getParameter("paymentId"));
                orderPaid.getUser().setPaypalPayerId(request.getParameter("PayerID"));
                orderProducer.sendNotification(new OrderDTO(orderPaid.getId(), orderPaid.getStatus()));
                redirect+="/tracking/" + orderPaid.getId();
            } else {
                orderPaid.setStatus("CANCELED");
                redirect+="/cancelled/" +orderId;
            }
            orderService.save(orderPaid);
            response.sendRedirect(redirect);
        } catch (Exception e) {
            try {
                response.sendRedirect(urlHome+"/cancelled");
            } catch (Exception ignored) {}
        }
    }

    @GetMapping("/cancelled")
    public void cancelled(@RequestParam("token") String token,
                                            @RequestParam("orderId") Long orderId,
                                            HttpServletResponse response) {
        try {

            Order orderToCancel = orderService.findById(orderId);
            orderToCancel.setStatus("CANCELED");
            orderService.save(orderToCancel);
            response.sendRedirect(urlHome+"/cart?message=cancelled");

        } catch (Exception e) {
            try {
                response.sendRedirect(urlHome + "/cart?message=errortocancel");
            } catch (Exception ignored) {
            }
        }
    }

}
