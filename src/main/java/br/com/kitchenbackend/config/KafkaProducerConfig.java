package br.com.kitchenbackend.config;

import br.com.kitchenbackend.dto.OrderDTO;
import br.com.kitchenbackend.dto.ProductDTO;
import br.com.kitchenbackend.producer.KafkaProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaProducerConfig {

    private final KafkaTemplate<String, OrderDTO> kafkaOrderTemplate;
    private final KafkaTemplate<String, ProductDTO> kafkaProductTemplate;

    public KafkaProducerConfig(KafkaTemplate<String, OrderDTO> kafkaOrderTemplate,
                               KafkaTemplate<String, ProductDTO> kafkaProductTemplate) {
        this.kafkaOrderTemplate = kafkaOrderTemplate;
        this.kafkaProductTemplate = kafkaProductTemplate;
    }

    @Bean
    public KafkaProducer<ProductDTO> productKafkaProducer() {
        return new KafkaProducer<>(kafkaProductTemplate, "new-product");
    }

    @Bean
    public KafkaProducer<OrderDTO> orderKafkaProducer() {
        return new KafkaProducer<>(kafkaOrderTemplate, "new-order");
    }

}
