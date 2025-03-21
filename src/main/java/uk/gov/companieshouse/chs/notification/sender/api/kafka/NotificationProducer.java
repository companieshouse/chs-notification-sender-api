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

    private void sendMessage(String topicName, byte[] message) throws NotificationSendingException {
        ProducerRecord<String, byte[]> producerRecord = new ProducerRecord<>(topicName, message);
        try {
            kafkaTemplate.send(producerRecord)
                .get(10, TimeUnit.SECONDS);
            LOG.infoContext(topicName, "Sent message=[" + Arrays.toString(message)
                + "]", null);
        } catch (ExecutionException | KafkaException ex) {
            LOG.errorContext(topicName, new Exception("Unable to send ["
                + Arrays.toString(message) + "] due to " + ex.getMessage() + " "
                + ex.getCause().getMessage()), null);
            throw new NotificationSendingException(
                "Unable to send " + topicName + " " + ex.getCause()
                    .getMessage(), ex.getCause());
        } catch (TimeoutException | InterruptedException |
                 java.util.concurrent.TimeoutException ex) {
            LOG.errorContext(topicName, new Exception("Unable to send ["
                + Arrays.toString(message) + "] due to " + ex.getMessage()), null);
            throw new NotificationSendingException(
                "Unable to send " + topicName + " " + ex.getMessage(), ex);
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