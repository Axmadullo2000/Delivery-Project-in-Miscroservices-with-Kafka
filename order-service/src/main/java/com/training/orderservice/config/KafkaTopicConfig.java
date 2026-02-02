package com.training.orderservice.config;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


@Configuration
public class KafkaTopicConfig {

    public static final String ORDER_EVENTS_TOPIC = "order-events";
    public static final String RESTAURANT_EVENTS_TOPIC = "restaurant-events";
    public static final String PAYMENT_EVENTS_TOPIC = "payment-events";
    public static final String DELIVERY_EVENTS_TOPIC = "delivery-events";
    public static final String REFUND_EVENTS_TOPIC = "refund-events";

    @Bean
    public NewTopic orderEventTopic() {
        return TopicBuilder
                .name(ORDER_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic restaurantEventTopic() {
        return TopicBuilder
            .name(RESTAURANT_EVENTS_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic paymentEventsTopic() {
        return TopicBuilder
            .name(PAYMENT_EVENTS_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic deliveryEventsTopic() {
        return TopicBuilder
                .name(DELIVERY_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic refundEventsTopic() {
        return TopicBuilder
                .name(REFUND_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
