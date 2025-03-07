package uk.gov.companieshouse.chs.notification.sender.api.producer;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;

public class KafkaProducerFactory {

    public KafkaProducer<String, byte[]> getProducer(Properties properties) {
        return new KafkaProducer<>(properties);
    }
}