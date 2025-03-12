package uk.gov.companieshouse.chs.notification.sender.api.translator;

import consumer.serialization.AvroSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkLetterDetailsRequest;
import uk.gov.companieshouse.chs.notification.sender.api.utils.StaticPropertyUtil;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.notification.ChsEmailNotification;
import uk.gov.companieshouse.notification.ChsLetterNotification;

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
        return avroSerializer.serialize(emailKafkaTopic, convertEmailRequestToAvroModel(govUkEmailDetailsRequest));
    }

    @Override
    public byte[] translateNotificationToLetterKafkaMessage(final GovUkLetterDetailsRequest govUkLetterDetailsRequest) {
        return avroSerializer.serialize(letterKafkaTopic, convertLetterRequestToAvroModel(govUkLetterDetailsRequest));
    }

    private ChsEmailNotification convertEmailRequestToAvroModel(final GovUkEmailDetailsRequest request) {
        return notificationMapper.mapToEmailDetailsRequest(request);
    }

    private ChsLetterNotification convertLetterRequestToAvroModel(final GovUkLetterDetailsRequest request) {
        return notificationMapper.mapToLetterDetailsRequest(request);
    }
    
}
