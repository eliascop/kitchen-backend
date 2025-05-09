package br.com.kitchenbackend.controller;

import br.com.kitchenbackend.model.ModelNotification;
import br.com.kitchenbackend.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kafka")
public class KafkaController {

    private final KafkaProducer<ModelNotification> orderKafkaProducer;
    private final KafkaProducer<ModelNotification> productKafkaProducer;

    @Autowired
    public KafkaController(KafkaProducer<ModelNotification> orderKafkaProducer,
                           KafkaProducer<ModelNotification> productKafkaProducer) {
        this.orderKafkaProducer = orderKafkaProducer;
        this.productKafkaProducer = productKafkaProducer;
    }

    @PostMapping("/publish/order-notification")
    public String publishOrderNotification(@RequestBody ModelNotification notification) {
        orderKafkaProducer.sendNotification(notification);
        return "Notificação de novo pedido com ID " + notification.getId() + " publicada no Kafka!";
    }

    @PostMapping("/publish/product-notification")
    public String publishProductNotification(@RequestBody ModelNotification notification) {
        productKafkaProducer.sendNotification(notification);
        return "Notificação de novo produto com ID " + notification.getId() + " publicada no Kafka!";
    }
}