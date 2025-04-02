package uk.gov.companieshouse.chs.notification.sender.api.kafka;

import org.apache.kafka.common.errors.TimeoutException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import uk.gov.companieshouse.kafka.producer.Acks;


class NotificationProducerIntegrationTests {

    private static final String EMAIL_TOPIC = "chs-notification-email";
    private static final String LETTER_TOPIC = "chs-notification-letter";
    private static final int TEST_RETRIES = 5;
    private static final int MINIMAL_MAX_BLOCK_MILLISECONDS = 10;
    private static final String BOOTSTRAP_SERVERS = "localhost:1234";

    private final byte[] myByte = new byte[]{0x1};

    KafkaProducerConfig config;
    KafkaTemplate<String, byte[]> kafkaTemplate;
    NotificationProducer notificationProducer;

    @BeforeEach
    void setup() {
        config = new KafkaProducerConfig(
            BOOTSTRAP_SERVERS,
            Acks.NO_RESPONSE.toString(),
            TEST_RETRIES,
            MINIMAL_MAX_BLOCK_MILLISECONDS);
        kafkaTemplate = config.kafkaTemplate();
        notificationProducer = new NotificationProducer(
            EMAIL_TOPIC,
            LETTER_TOPIC,
            config,
            kafkaTemplate);
    }

    @Test
    void testingExceptionFromKafkaClientLibraryWhenSendingEmailAndKafkaServiceIsAbsent() {
        try {
            notificationProducer.sendEmail(myByte);
        } catch (NotificationSendingException e) {
            Assertions.assertInstanceOf(TimeoutException.class, e.getCause());
            return;
        }
        Assertions.fail("A timeout exception should have been thrown");
    }

    @Test
    void testingExceptionFromKafkaClientLibraryWhenSendingLetterAndKafkaServiceIsAbsent() {
        try {
            notificationProducer.sendLetter(myByte);
        } catch (NotificationSendingException e) {
            Assertions.assertInstanceOf(TimeoutException.class, e.getCause());
            return;
        }
        Assertions.fail("A timeout exception should have been thrown");
    }
}
