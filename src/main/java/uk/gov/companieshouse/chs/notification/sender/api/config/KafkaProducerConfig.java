package uk.gov.companieshouse.chs.notification.sender.api.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;


@Configuration
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String brokerAddress;

    @Value("${kafka.config.acks}")
    private String acks = "ack";

    @Value("${kafka.max-attempts}")
    private Integer retries;


    @Bean
    public ProducerFactory<String, byte[]> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
            brokerAddress.split(","));
        configProps.put(
            ProducerConfig.ACKS_CONFIG, acks);
        configProps.put(
            ProducerConfig.RETRIES_CONFIG, retries);
        /*
        configProps.put(
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            AvroSerializer.class);
        configProps.put(
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class);
         */
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, byte[]> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}