package uk.gov.companieshouse.chs.notification.sender.api.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.chs.notification.sender.api.utils.StaticPropertyUtil;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class NotificationProducer {

    private static final Logger LOG = LoggerFactory.getLogger(StaticPropertyUtil.APPLICATION_NAMESPACE);

    @Value("${kafka.topic.email}")
    private String emailTopic;
    @Value("${kafka.topic.letter}")
    private String letterTopic;
    private byte[] emailData;


    private KafkaProducer<String, byte[]> producer;

    NotificationProducer() {
    }

    public NotificationProducer(ProducerConfig config) {
        this(config, new KafkaProducerFactory());
    }

    public NotificationProducer(ProducerConfig config, KafkaProducerFactory producerFactory) {
        Properties props = new Properties();

        props.put("bootstrap.servers", String.join(",", config.getBrokerAddresses()));
        props.put("acks", config.getAcks().getCode());
        props.put("key.serializer", config.getKeySerializer());
        props.put("value.serializer", config.getValueSerializer());
        props.put("retries", config.getRetries());
        props.put("max.block.ms", config.getMaxBlockMilliseconds());
        props.put("request.timeout.ms", config.getRequestTimeoutMilliseconds());
        props.put("enable.idempotence", config.isEnableIdempotence());

        if (config.isRoundRobinPartitioner()) {
            props.put("partition.assignment.strategy", "roundrobin");
        }

        producer = producerFactory.getProducer(props);
    }
     /**
     * Sends an email-send message to the Kafka producer.
     */
    public void sendEmail(byte[] emailData, String emailTopic) throws NotificationSendingException, ExecutionException, InterruptedException {

        ProducerRecord<String, byte[]> emailRecord = new ProducerRecord<>(emailTopic, emailData);

        LOG.info("Sending to message to " + emailTopic);

        // send data
        producer.send(emailRecord).get();

        // tell producer to send all data and block until complete
        producer.flush();

        // close the producer
        producer.close();

    }
     /**
     * Sends a letter-send message to the Kafka producer.
     */
    public void sendLetter(byte[] letterData, String letterTopic) throws NotificationSendingException, ExecutionException, InterruptedException  {

        ProducerRecord<String, byte[]> letterRecord = new ProducerRecord<>(letterTopic, letterData);
        LOG.info("Sending to message to " + letterTopic);

        // send data
        producer.send(letterRecord);

        // tell producer to send all data and block until complete
        producer.flush();

        // close the producer
        producer.close();
    }

    public Future<RecordMetadata> sendAndReturnFuture(String emailTopic, byte[] emailData) {

        ProducerRecord<String, byte[]> record =new ProducerRecord<>(emailTopic, emailData);

        return producer.send(record);
    }
    public void close() {
        producer.close();
    }
}