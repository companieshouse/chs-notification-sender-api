package uk.gov.companieshouse.chs.notification.sender.api.restapi;

import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkLetterDetailsRequest;
import uk.gov.companieshouse.chs.notification.sender.api.producer.NotificationProducer;
import uk.gov.companieshouse.chs.notification.sender.api.translator.KafkaTranslatorInterface;
import uk.gov.companieshouse.chs.notification.sender.api.utils.StaticPropertyUtil;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
class NotificationService {

    private final KafkaTranslatorInterface kafkaMessageTranslator;

    private final NotificationProducer notificationProducer;

    private static final Logger LOG = LoggerFactory.getLogger( StaticPropertyUtil.APPLICATION_NAMESPACE );

    NotificationService(KafkaTranslatorInterface kafkaMessageTranslator,
        NotificationProducer notificationProducer ) {
        this.kafkaMessageTranslator = kafkaMessageTranslator;
        this.notificationProducer = notificationProducer;
    }

    public byte[] translateEmailNotification(final GovUkEmailDetailsRequest request) {
       return kafkaMessageTranslator.translateNotificationToEmailKafkaMessage(request);
    }

    public byte[] translateLetterNotification(final GovUkLetterDetailsRequest request) {
        return kafkaMessageTranslator.translateNotificationToLetterKafkaMessage(request);
    }

    public void sendEmail(byte[] email){
        notificationProducer.sendEmail(email);
    }
    public void sendLetter(byte[] letter){
        notificationProducer.sendLetter(letter);
    }

}
