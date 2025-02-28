package uk.gov.companieshouse.chs.notification.sender.api.service;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkLetterDetailsRequest;
import uk.gov.companieshouse.chs.notification.sender.api.kafka.KafkaTranslatorInterface;
import uk.gov.companieshouse.chs.notification.sender.api.utils.StaticPropertyUtil;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class NotificationService {

    private final KafkaTranslatorInterface kafkaMessageTranslator;

    private static final Logger LOG = LoggerFactory.getLogger( StaticPropertyUtil.APPLICATION_NAMESPACE );

    NotificationService(KafkaTranslatorInterface kafkaMessageTranslator) {
        this.kafkaMessageTranslator = kafkaMessageTranslator;
    }

    public byte[] translateEmailNotification(final GovUkEmailDetailsRequest request) {
       return kafkaMessageTranslator.translateNotificationToEmailKafkaMessage(request);
    }

    public byte[] translateLetterNotification(final GovUkLetterDetailsRequest request) {
        return kafkaMessageTranslator.translateNotificationToLetterKafkaMessage(request);
    }

}
