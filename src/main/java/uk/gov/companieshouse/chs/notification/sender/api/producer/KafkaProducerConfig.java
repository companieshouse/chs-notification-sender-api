package uk.gov.companieshouse.chs.notification.sender.api.producer;

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
    @Value("${kafka.broker.addr}")
    private String brokerAddr;

    @Value("${kafka.config.acks}")
    private String acks;

    @Value("${kafka.config.retries}")
    private int retries;

    @Value("${kafka.config.is.round.robin}")
    private boolean isRoundRobin;

    @Bean
    public ProducerFactory<String, byte[]> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
            brokerAddr.split(","));
        configProps.put(
            ProducerConfig.ACKS_CONFIG, acks);
        configProps.put(
            ProducerConfig.RETRIES_CONFIG, retries);
        /*
        configProps.put(
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class);
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