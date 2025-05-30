package br.com.kitchen.backend.controller;

import br.com.kitchen.backend.dto.OrderDTO;
import br.com.kitchen.backend.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kafka")
public class KafkaController {

    private final KafkaProducer<OrderDTO> orderKafkaProducer;
    private final KafkaProducer<OrderDTO> productKafkaProducer;

    @Autowired
    public KafkaController(KafkaProducer<OrderDTO> orderKafkaProducer,
                           KafkaProducer<OrderDTO> productKafkaProducer) {
        this.orderKafkaProducer = orderKafkaProducer;
        this.productKafkaProducer = productKafkaProducer;
    }

    @PostMapping("/publish/order-notification")
    public String publishOrderNotification(@RequestBody OrderDTO notification) {
        orderKafkaProducer.sendNotification(notification);
        return "Notificação de novo pedido com ID " + notification.getId() + " publicada no Kafka!";
    }

    @PostMapping("/publish/product-notification")
    public String publishProductNotification(@RequestBody OrderDTO notification) {
        productKafkaProducer.sendNotification(notification);
        return "Notificação de novo produto com ID " + notification.getId() + " publicada no Kafka!";
    }
}