package br.com.kitchenbackend.config;

import br.com.kitchenbackend.model.ModelNotification;
import br.com.kitchenbackend.producer.KafkaProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaConfig {

    private final KafkaTemplate<String, ModelNotification> kafkaTemplate;

    public KafkaConfig(KafkaTemplate<String, ModelNotification> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Bean
    public KafkaProducer<ModelNotification> productKafkaProducer() {
        return new KafkaProducer<>(kafkaTemplate, "new-product");
    }

    @Bean
    public KafkaProducer<ModelNotification> orderKafkaProducer() {
        return new KafkaProducer<>(kafkaTemplate, "new-order");
    }
}
