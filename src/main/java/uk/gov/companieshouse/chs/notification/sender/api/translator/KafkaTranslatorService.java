package uk.gov.companieshouse.chs.notification.sender.api.translator;

import com.fasterxml.jackson.core.JsonProcessingException;
import consumer.serialization.AvroSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkLetterDetailsRequest;
import uk.gov.companieshouse.chs.notification.sender.api.utils.StaticPropertyUtil;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
class KafkaTranslatorService implements KafkaTranslatorInterface {

    private final String emailKafkaTopic;
    private final String letterKafkaTopic;
    private final AvroSerializer avroSerializer;
    private final NotificationMapper notificationMapper;

    public KafkaTranslatorService(@Value("${kafka.topic.email}") String emailTopic, @Value("${kafka.topic.letter}") String letterTopic, AvroSerializer avroSerializer, NotificationMapper notificationMapper) {
        this.emailKafkaTopic = emailTopic;
        this.letterKafkaTopic = letterTopic;
        this.avroSerializer = avroSerializer;
        this.notificationMapper = notificationMapper;
    }

    private static final Logger LOG = LoggerFactory.getLogger(StaticPropertyUtil.APPLICATION_NAMESPACE);


    @Override
    public byte[] translateNotificationToEmailKafkaMessage(final GovUkEmailDetailsRequest govUkEmailDetailsRequest) {
        return avroSerializer.serialize(emailKafkaTopic, convertEmailRequestToJson(govUkEmailDetailsRequest));
    }

    @Override
    public byte[] translateNotificationToLetterKafkaMessage(final GovUkLetterDetailsRequest govUkLetterDetailsRequest) {
        return avroSerializer.serialize(letterKafkaTopic, convertLetterRequestToJson(govUkLetterDetailsRequest));
    }

    private String convertEmailRequestToJson(final GovUkEmailDetailsRequest request) {
        try {
            final var emailDetailsRequest = notificationMapper.mapToEmailDetailsRequest(request);
            return emailDetailsRequest.convertToJson();
        } catch (JsonProcessingException e) {
            LOG.error("Error while mapping GovUkEmailDetailsRequest", e);
            throw new IllegalArgumentException("Invalid message format");
        }
    }

    private String convertLetterRequestToJson(final GovUkLetterDetailsRequest request) {
        try {
            final var letterDetailsRequest = notificationMapper.mapToLetterDetailsRequest(request);
            return letterDetailsRequest.convertToJson();
        } catch (JsonProcessingException e) {
            LOG.error("Error while mapping GovUkLetterDetailsRequest", e);
            throw new IllegalArgumentException("Invalid message format");
        }
    }

}
