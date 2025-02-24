package uk.gov.companieshouse.chs.notification.config;

import consumer.serialization.AvroSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.gov.companieshouse.chs.notification.translator.KafkaTranslatorService;

@Configuration
@Import(AvroConfig.class)
public class NotificationServiceConfig {

    @Value("${kafka.topic.email}")
    private String emailKafkaTopic;

    @Value("${kafka.topic.letter}")
    private String letterKafkaTopic;

    @Bean
    public KafkaTranslatorService kafkaTranslatorService(AvroSerializer avroSerializer){
        return new KafkaTranslatorService(emailKafkaTopic,letterKafkaTopic,avroSerializer);
    }
}
