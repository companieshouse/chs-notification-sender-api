package uk.gov.companieshouse.chs.notification.sender.api.translator;

import consumer.serialization.AvroSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AvroConfig {

    @Bean
    public AvroSerializer avroSerializer() {
        return new AvroSerializer();
    }
}
