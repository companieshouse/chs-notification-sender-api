package uk.gov.companieshouse.chs.notification.sender.api.restapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkLetterDetailsRequest;
import uk.gov.companieshouse.chs.notification.sender.api.translator.KafkaTranslatorInterface;
import uk.gov.companieshouse.chs.notification.sender.api.utils.StaticPropertyUtil;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
class EmailLetterService {

    private final KafkaTranslatorInterface kafkaMessageTranslator;

    private final NotificationMapper notificationMapper;

    private static final Logger LOG = LoggerFactory.getLogger( StaticPropertyUtil.APPLICATION_NAMESPACE );

    public EmailLetterService(KafkaTranslatorInterface kafkaMessageTranslator, NotificationMapper notificationMapper) {
        this.kafkaMessageTranslator = kafkaMessageTranslator;
        this.notificationMapper = notificationMapper;
    }

    public byte[] translateEmailNotification(final GovUkEmailDetailsRequest request) {
       return kafkaMessageTranslator.translateNotificationToEmailKafkaMessage(convertEmailRequestToJson(request));
    }

    public byte[] translateLetterNotification(final GovUkLetterDetailsRequest request) {
        return kafkaMessageTranslator.translateNotificationToLetterKafkaMessage(convertLetterRequestToJson(request));
    }

    private String convertEmailRequestToJson(final GovUkEmailDetailsRequest request) {
        try {
            final var emailDetailsRequest = notificationMapper.mapToEmailDetailsRequest(request);
            return emailDetailsRequest.convertToJson();
        } catch (JsonProcessingException e) {
            LOG.error("Error while mapping GovUkEmailDetailsRequest", e);
            throw new IllegalArgumentException( "Invalid message format" );
        }
    }

    private String convertLetterRequestToJson(final GovUkLetterDetailsRequest request) {
        try {
            final var letterDetailsRequest = notificationMapper.mapToLetterDetailsRequest(request);
            return letterDetailsRequest.convertToJson();
        } catch (JsonProcessingException e) {
            LOG.error("Error while mapping GovUkLetterDetailsRequest", e);
            throw new IllegalArgumentException( "Invalid message format" );
        }
    }

}
