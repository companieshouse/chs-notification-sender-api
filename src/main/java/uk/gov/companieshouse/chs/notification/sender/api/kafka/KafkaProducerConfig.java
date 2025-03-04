package uk.gov.companieshouse.chs.notification.sender.api.kafka;

import consumer.serialization.AvroSerializer;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import uk.gov.companieshouse.kafka.producer.Acks;

@Configuration
class KafkaProducerConfig {

    private final String brokerAddress;
    private final String acknowledgementsName;
    private final int retries;
    private final int maxBlockMilliseconds;

    public KafkaProducerConfig(
        @Value("${spring.kafka.bootstrap-servers}") String brokerAddress,
        @Value("${kafka.config.acks}") String acknowledgementsName,
        @Value("${kafka.max-attempts}") int retries,
        @Value("${kafka.max-block-milliseconds}") int maxBlockMilliseconds) {
        this.brokerAddress = brokerAddress;
        this.acknowledgementsName = acknowledgementsName;
        this.retries = retries;
        this.maxBlockMilliseconds = maxBlockMilliseconds;
    }

    @Bean
    public ProducerFactory<String, byte[]> producerFactory() {
        var acknowledgements = Acks.valueOf(acknowledgementsName);
        Map<String, Object> configProps = Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerAddress,
            ProducerConfig.ACKS_CONFIG, acknowledgements.getCode(),
            ProducerConfig.RETRIES_CONFIG, retries,
            ProducerConfig.MAX_BLOCK_MS_CONFIG, maxBlockMilliseconds,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, AvroSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroSerializer.class
        );
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, byte[]> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}