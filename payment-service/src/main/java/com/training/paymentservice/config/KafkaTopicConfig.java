package com.training.paymentservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


@Configuration
public class KafkaTopicConfig {

    public static final String PAYMENT_EVENTS_TOPIC = "payment-events";
    public static final String REFUND_EVENTS_TOPIC = "refund-events";

    @Bean
    public NewTopic paymentEventsTopic() {
        return TopicBuilder.name(PAYMENT_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic refundEventsTopic() {
        return TopicBuilder.name(REFUND_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic refundEventsDltTopic() {
        return TopicBuilder.name(REFUND_EVENTS_TOPIC + ".DLT")
                .partitions(1)
                .replicas(1)
                .build();
    }

}
