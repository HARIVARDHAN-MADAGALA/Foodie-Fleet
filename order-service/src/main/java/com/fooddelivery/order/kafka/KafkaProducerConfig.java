package com.fooddelivery.order.kafka;

import com.fooddelivery.order.event.OrderEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * KAFKA PRODUCER CONFIGURATION - Order Service
 *
 * This configuration sets up Kafka producers for publishing order events.
 *
 * Producer Configuration:
 * 1. Serializer - Converts Java objects to bytes for Kafka
 * 2. Bootstrap Servers - Kafka broker addresses
 * 3. Acks - Acknowledgment level for reliability
 * 4. Retries - Number of retry attempts on failure
 *
 * How it works:
 * - KafkaTemplate uses this configuration to send messages
 * - Messages are serialized to JSON automatically
 * - Kafka stores messages and delivers to consumers
 */
@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    /**
     * PRODUCER CONFIGURATION PROPERTIES
     */
    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();

        // Kafka server address
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Serializers for key and value
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // Wait for all replicas to acknowledge (most reliable)
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        // Number of retries on failure
        props.put(ProducerConfig.RETRIES_CONFIG, 3);

        // Batch size for better performance
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);

        // Wait time before sending batch
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);

        // Buffer memory for producer
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);

        return props;
    }

    /**
     * PRODUCER FACTORY
     *
     * Creates Kafka producers with the configured properties
     */
    @Bean
    public ProducerFactory<String, OrderEvent> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    /**
     * KAFKA TEMPLATE
     *
     * Main API for sending messages to Kafka.
     * Used by OrderEventProducer to publish events.
     */
    @Bean
    public KafkaTemplate<String, OrderEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
