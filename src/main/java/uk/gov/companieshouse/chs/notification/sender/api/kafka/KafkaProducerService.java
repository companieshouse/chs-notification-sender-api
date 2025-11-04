package uk.gov.companieshouse.chs.notification.sender.api.kafka;

import static com.google.common.net.HttpHeaders.X_REQUEST_ID;
import static uk.gov.companieshouse.chs.notification.sender.api.ChsNotificationSenderApiApplication.APPLICATION_NAMESPACE;

import consumer.serialization.AvroSerializer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.chs.notification.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.api.chs.notification.model.GovUkLetterDetailsRequest;
import uk.gov.companieshouse.chs.notification.sender.api.config.ApplicationConfig;
import uk.gov.companieshouse.chs.notification.sender.api.exception.NotificationException;
import uk.gov.companieshouse.chs.notification.sender.api.mapper.NotificationMapper;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.logging.util.DataMap;
import uk.gov.companieshouse.notification.ChsEmailNotification;
import uk.gov.companieshouse.notification.ChsLetterNotification;

@Service
public class KafkaProducerService {

    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

    private final AvroSerializer avroSerializer = new AvroSerializer();
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final NotificationMapper notificationMapper;
    private final String emailTopic;
    private final String letterTopic;

    public KafkaProducerService(
        final KafkaTemplate<String, byte[]> kafkaTemplate,
        final NotificationMapper notificationMapper,
        final ApplicationConfig applicationConfig
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.notificationMapper = notificationMapper;
        this.emailTopic = applicationConfig.getEmailTopic();
        this.letterTopic = applicationConfig.getLetterTopic();
    }

    public void sendEmail(final GovUkEmailDetailsRequest govUkEmailDetailsRequest,
                          final String contextId)
        throws NotificationException {
        LOG.debug("Mapping email request to Avro format");
        ChsEmailNotification chsEmailNotification = notificationMapper.mapToEmailDetailsRequest(
            govUkEmailDetailsRequest);
        sendMessage(emailTopic,
                    avroSerializer.serialize(emailTopic, chsEmailNotification),
                    contextId);
    }

    public void sendLetter(final GovUkLetterDetailsRequest govUkLetterDetailsRequest,
                           final String contextId)
        throws NotificationException {
        LOG.debug("Mapping letter request to Avro format");
        ChsLetterNotification chsLetterNotification = notificationMapper.mapToLetterDetailsRequest(
            govUkLetterDetailsRequest);
        sendMessage(letterTopic,
                    avroSerializer.serialize(letterTopic, chsLetterNotification),
                    contextId);
    }

    private void sendMessage(final String topic, final byte[] message, final String contextId)
        throws NotificationException {

        var logMap = new DataMap.Builder()
                .topic(topic)
                .contextId(contextId)
                .build()
                .getLogMap();

        LOG.debug(String.format("Sending message to topic: %s", topic), logMap);

        ProducerRecord<String, byte[]> producerRecord = createProducerRecord(topic, message);
        if (contextId != null) {
            LOG.info("Propagating contextId: " + contextId, logMap);
            producerRecord.headers().add(X_REQUEST_ID, contextId.getBytes());
        }

        try {
            kafkaTemplate.send(producerRecord).get(10, TimeUnit.SECONDS);
            LOG.info(String.format("Successfully sent message to topic: %s", topic), logMap);
        } catch (ExecutionException | TimeoutException | InterruptedException ex) {
            if (ex.getCause() instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            LOG.error(String.format("Failed to send message to topic: %s", topic), ex, logMap);
            throw new NotificationException(
                String.format("Failed to send notification message to topic: %s", topic), ex);
        }
    }

    protected ProducerRecord<String, byte[]> createProducerRecord(final String topic,
                                                                  final byte[] message) {
        return new ProducerRecord<>(topic, message);
    }

}
