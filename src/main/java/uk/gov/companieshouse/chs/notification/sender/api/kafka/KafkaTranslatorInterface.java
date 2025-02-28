package uk.gov.companieshouse.chs.notification.sender.api.kafka;

import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkLetterDetailsRequest;

/**
 * This public interface is used to translate the REST API notification messages to kafka messages for the topics
 * chs-notification-email
 * chs-notification-letter
 */
public interface KafkaTranslatorInterface {

    /**
     * Use this method to translate CHS Notification message to kafka message
     * To be posted to chs-notification-email topic
     *
     * @param govUkEmailDetailsRequest
     * @return byte[] of serialised avro data
     */
    byte[] translateNotificationToEmailKafkaMessage( final GovUkEmailDetailsRequest govUkEmailDetailsRequest );


    /**
     * Use this method to translate CHS Notification message to kafka message
     * To be posted to chs-notification-letter topic
     *
     * @param govUkLetterDetailsRequest
     * @return byte[] of serialised avro data
     */
    byte[] translateNotificationToLetterKafkaMessage( final GovUkLetterDetailsRequest govUkLetterDetailsRequest );
}
