package uk.gov.companieshouse.chs.notification.sender.api.producer;

import java.util.Properties;

import org.apache.kafka.clients.producer.Producer;

/**
 * Factory for creating producers to write messages to topics in Kafka
 */
public interface ProducerFactory<K, V> {

    /**
     * Create an instance of a producer.
     *
     * @return producer
     */
    Producer<K, V> getProducer(Properties properties);
}