package com.fooddelivery.payment.kafka;

import com.fooddelivery.order.event.OrderEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * KAFKA CONSUMER CONFIGURATION
 *
 * This configuration sets up Kafka consumers for the Payment Service.
 *
 * Key Concepts:
 * 1. Consumer Factory - Creates Kafka consumers
 * 2. Listener Container - Manages listener threads
 * 3. Deserializer - Converts Kafka messages to Java objects
 * 4. Group ID - Identifies consumer group (for load balancing)
 *
 * How Kafka Consumer Groups Work:
 * - Multiple consumers with same group ID share the workload
 * - Each message is delivered to only ONE consumer in the group
 * - Enables horizontal scaling
 */
@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    /**
     * CONSUMER CONFIGURATION PROPERTIES
     *
     * These properties configure how the Kafka consumer behaves
     */
    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();

        // Kafka server address
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Consumer group ID (all Payment Service instances share this)
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "payment-service-group");

        // Start reading from earliest message if no offset exists
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Automatically commit offsets (marks message as processed)
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);

        return props;
    }

    /**
     * CONSUMER FACTORY
     *
     * Creates Kafka consumers with the configured properties
     */
    @Bean
    public ConsumerFactory<String, OrderEvent> consumerFactory() {
        JsonDeserializer<OrderEvent> deserializer = new JsonDeserializer<>(OrderEvent.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(false);

        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                deserializer
        );
    }

    /**
     * KAFKA LISTENER CONTAINER FACTORY
     *
     * Creates the container that manages Kafka listener threads.
     * This enables concurrent processing of messages.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());

        // Number of concurrent consumer threads (for parallel processing)
        factory.setConcurrency(3);

        return factory;
    }
}
