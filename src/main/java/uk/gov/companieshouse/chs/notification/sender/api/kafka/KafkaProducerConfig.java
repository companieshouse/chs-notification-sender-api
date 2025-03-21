package uk.gov.companieshouse.chs.notification.sender.api.kafka;

import consumer.serialization.AvroSerializer;
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
class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String brokerAddress;

    @Value("${kafka.config.acks}")
    private String acksString;

    @Value("${kafka.max-attempts}")
    private Integer retries;

    @Value("${kafka.max-block-milliseconds}")
    private Integer maxBlockMilliseconds;

    public void setBrokerAddress(String brokerAddress) {
        this.brokerAddress = brokerAddress;
    }

    public void setAcks(Acks acks) {
        this.acksString = acks.toString();
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public void setMaxBlockMilliseconds(Integer milliseconds) {
        this.maxBlockMilliseconds = milliseconds;
    }

    /*
        public KafkaProducerConfig() {
        }
    */
    @Bean
    public ProducerFactory<String, byte[]> producerFactory() {
        Acks acks = Acks.valueOf(acksString);
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerAddress);
        configProps.put(
            ProducerConfig.ACKS_CONFIG, acks.getCode());
        configProps.put(
            ProducerConfig.RETRIES_CONFIG, retries);
        configProps.put(
            ProducerConfig.MAX_BLOCK_MS_CONFIG, maxBlockMilliseconds);
        configProps.put(
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            AvroSerializer.class);
        configProps.put(
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            AvroSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, byte[]> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}