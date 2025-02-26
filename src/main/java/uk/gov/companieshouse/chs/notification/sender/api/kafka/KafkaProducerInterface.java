package uk.gov.companieshouse.chs.notification.sender.api.kafka;

/**
 *  Use this interface to send messages to
 *   * chs-notification-email
 *   * chs-notification-letter
 */
public interface KafkaProducerInterface {
    /**
     *
     * @param emailRequest - serialised email avro message
     */
    void sendToEmailTopic(byte[] emailRequest);

    /**
     *
     * @param letterRequest - serialised letter avro message
     */
    void sendToLetterTopic(byte[] letterRequest);
}
