package uk.gov.companieshouse.chs.notification.translator;

/**
 *  This public interface is used to translate the REST API notification messages to kafka messages for the topics
 *  chs-notification-email
 *  chs-notification-letter
 */
public interface NotificationSenderKafkaTranslatorInterface {

    /**
     *  Use this method to translate CHS Notification message to kafka message
     *  To be posted to chs-notification-email topic
     *  params emailNotificationMessage in json format
     *  returns byte[] of serialised avro data
     */
    byte[] translateNotificationToEmailKafkaMessage(String emailNotificationMessage);


    /**
     *  Use this method to translate CHS Notification message to kafka message
     *  To be posted to chs-notification-letter topic
     *  params letterNotificationMessage in json format
     *  returns byte[] of serialised avro data
     */
    byte[] translateNotificationToLetterKafkaMessage(String letterNotificationMessage);
}
