package com.training.deliveryservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


@Configuration
public class KafkaTopicConfig {

    public static final String PAYMENT_EVENTS_TOPIC = "payment-events";
    public static final String DELIVERY_EVENTS_TOPIC = "delivery-events";
    public static final String RESTAURANT_EVENTS_TOPIC = "restaurant-events";

    @Bean
    public NewTopic deliveryEventsTopic() {
        return TopicBuilder.name(DELIVERY_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic deliveryEventsDltTopic() {
        return TopicBuilder.name(DELIVERY_EVENTS_TOPIC + ".DLT")
                .partitions(1)
                .replicas(1)
                .build();
    }

}
