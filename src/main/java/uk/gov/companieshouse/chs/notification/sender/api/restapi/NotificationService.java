package uk.gov.companieshouse.chs.notification.sender.api.restapi;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkLetterDetailsRequest;
import uk.gov.companieshouse.chs.notification.sender.api.kafka.KafkaProducerInterface;
import uk.gov.companieshouse.chs.notification.sender.api.kafka.NotificationSendingException;
import uk.gov.companieshouse.chs.notification.sender.api.translator.KafkaTranslatorInterface;

@Service
class NotificationService {

    private final KafkaTranslatorInterface kafkaMessageTranslator;
    private final KafkaProducerInterface notificationProducer;

    NotificationService(KafkaTranslatorInterface kafkaMessageTranslator,
        KafkaProducerInterface notificationProducer) {
        this.kafkaMessageTranslator = kafkaMessageTranslator;
        this.notificationProducer = notificationProducer;
    }

    public byte[] translateEmailNotification(final GovUkEmailDetailsRequest request) {
        return kafkaMessageTranslator.translateNotificationToEmailKafkaMessage(request);
    }

    public byte[] translateLetterNotification(final GovUkLetterDetailsRequest request) {
        return kafkaMessageTranslator.translateNotificationToLetterKafkaMessage(request);
    }

    public void sendEmail(byte[] email) throws NotificationSendingException {
        notificationProducer.sendEmail(email);
    }

    public void sendLetter(byte[] letter) throws NotificationSendingException {
        notificationProducer.sendLetter(letter);
    }

}
