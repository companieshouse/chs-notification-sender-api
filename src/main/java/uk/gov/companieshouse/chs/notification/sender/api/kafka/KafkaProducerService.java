package uk.gov.companieshouse.chs.notification.sender.api.kafka;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkLetterDetailsRequest;
import uk.gov.companieshouse.chs.notification.sender.api.exception.NotificationException;
import uk.gov.companieshouse.chs.notification.sender.api.mapper.NotificationMapper;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.kafka.serialization.AvroSerializer;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.notification.ChsEmailNotification;
import uk.gov.companieshouse.notification.ChsLetterNotification;

import static uk.gov.companieshouse.chs.notification.sender.api.config.ApplicationConfig.APPLICATION_NAMESPACE;
import static uk.gov.companieshouse.chs.notification.sender.api.config.ApplicationConfig.EMAIL_TOPIC;
import static uk.gov.companieshouse.chs.notification.sender.api.config.ApplicationConfig.LETTER_TOPIC;

@Service
public class KafkaProducerService {
    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final NotificationMapper notificationMapper;

    public KafkaProducerService(
            final KafkaTemplate<String, byte[]> kafkaTemplate,
            final NotificationMapper notificationMapper
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.notificationMapper = notificationMapper;
    }

    public void sendEmail(final GovUkEmailDetailsRequest govUkEmailDetailsRequest) throws NotificationException {
        LOG.debug("Mapping email request to Avro format");
        var chsEmailNotification = notificationMapper.mapToEmailDetailsRequest(govUkEmailDetailsRequest);
        AvroSerializer<ChsEmailNotification> avroSerializer = new AvroSerializer<>(new SpecificDatumWriter<>(), EncoderFactory.get());
        try {
            sendMessage(EMAIL_TOPIC, avroSerializer.toBinary(chsEmailNotification));
        } catch (SerializationException e) {
            throw new NotificationException(e.getMessage(), e);
        }
    }

    public void sendLetter(final GovUkLetterDetailsRequest govUkLetterDetailsRequest) throws NotificationException {
        LOG.debug("Mapping letter request to Avro format");
        var chsLetterNotification = notificationMapper.mapToLetterDetailsRequest(govUkLetterDetailsRequest);
        AvroSerializer<ChsLetterNotification> avroSerializer = new AvroSerializer<>(new SpecificDatumWriter<>(), EncoderFactory.get());
        try {
            sendMessage(LETTER_TOPIC, avroSerializer.toBinary(chsLetterNotification));
        } catch (SerializationException e) {
            throw new NotificationException(e.getMessage(), e);
        }
    }

    private void sendMessage(final String topic, final byte[] message) throws NotificationException {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("topic", topic);

        LOG.debug(String.format("Sending message to topic: %s", topic), logMap);
        ProducerRecord<String, byte[]> producerRecord = new ProducerRecord<>(topic, message);

        try {
            kafkaTemplate.send(producerRecord).get(10, TimeUnit.SECONDS);
            LOG.info(String.format("Successfully sent message to topic: %s", topic), logMap);
        } catch (ExecutionException | TimeoutException | InterruptedException ex) {
            if (ex.getCause() instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            
            LOG.error(String.format("Failed to send message to topic: %s", topic), ex, logMap);
            throw new NotificationException(String.format("Failed to send notification message to topic: %s", topic), ex);
        }
    }
}
