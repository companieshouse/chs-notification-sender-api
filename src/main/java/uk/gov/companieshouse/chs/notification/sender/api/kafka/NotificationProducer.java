package uk.gov.companieshouse.chs.notification.sender.api.kafka;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.TimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.chs.notification.sender.api.utils.StaticPropertyUtil;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;


@Service
class NotificationProducer implements KafkaProducerInterface {

    // Repeated strings
    static final String UNABLE_TO_SEND_OPEN = "Unable to send [";
    static final String CLOSE_DUE_TO = "] due to ";
    static final String UNABLE_TO_SEND_SPACE = "Unable to send ";
    static final String SPACE = " ";

    private static final Logger LOG = LoggerFactory
        .getLogger(StaticPropertyUtil.APPLICATION_NAMESPACE);
    @Autowired
    private final KafkaProducerConfig kafkaProducerConfig;
    @Autowired
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    @Value("${kafka.topic.email}")
    private final String emailTopic;
    @Value("${kafka.topic.letter}")
    private final String letterTopic;
    
    public NotificationProducer(@Value("${kafka.topic.email}") String emailTopic,
        @Value("${kafka.topic.letter}") String letterTopic,
        KafkaProducerConfig config, KafkaTemplate<String, byte[]> template) {
        this.emailTopic = emailTopic;
        this.letterTopic = letterTopic;
        this.kafkaProducerConfig = config;
        this.kafkaTemplate = template;
    }

    private void sendMessage(final String topicName, final byte[] message)
        throws NotificationSendingException {
        ProducerRecord<String, byte[]> producerRecord = new ProducerRecord<>(topicName, message);
        final var messageAsString = Arrays.toString(message);
        try {
            kafkaTemplate.send(producerRecord)
                .get(10, TimeUnit.SECONDS);
            LOG.infoContext(topicName, "Sent message=[" + messageAsString
                + "]", null);
        } catch (ExecutionException | KafkaException ex) {
            LOG.errorContext(topicName, new Exception(UNABLE_TO_SEND_OPEN
                + messageAsString + CLOSE_DUE_TO + ex.getMessage() + SPACE
                + ex.getCause().getMessage()), null);
            throw new NotificationSendingException(
                UNABLE_TO_SEND_SPACE + topicName + SPACE + ex.getCause()
                    .getMessage(), ex.getCause());
        } catch (TimeoutException |
                 java.util.concurrent.TimeoutException ex) {
            LOG.errorContext(topicName, new Exception(UNABLE_TO_SEND_OPEN
                + messageAsString + CLOSE_DUE_TO + ex.getMessage()), null);
            throw new NotificationSendingException(
                UNABLE_TO_SEND_SPACE + topicName + SPACE + ex.getMessage(), ex);
        } catch (InterruptedException ex) {
            LOG.errorContext(topicName, new Exception(UNABLE_TO_SEND_OPEN
                + messageAsString + CLOSE_DUE_TO + ex.getMessage()), null);
            Thread.currentThread().interrupt();
        }

    }

    /**
     * Sends an email-send message to the Kafka producer.
     */
    public void sendEmail(byte[] emailData) throws NotificationSendingException {
        sendMessage(emailTopic, emailData);
    }

    /**
     * Sends a letter-send message to the Kafka producer.
     */
    public void sendLetter(byte[] letterData) throws NotificationSendingException {
        sendMessage(letterTopic, letterData);
    }

}