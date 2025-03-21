package uk.gov.companieshouse.chs.notification.sender.api.kafka;

import org.apache.kafka.common.errors.TimeoutException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;


public class NotificationProducerIntegrationTests {

    private static final String EMAIL_TOPIC = "chs-notification-email";
    private static final String LETTER_TOPIC = "chs-notification-letter";

    private final byte[] myByte = new byte[]{0x1};

    String bootstrapServers = "localhost:1234";
    KafkaProducerConfig config;

    KafkaTemplate<String, byte[]> kafkaTemplate;
    NotificationProducer notificationProducer;


    @BeforeEach
    void setup() {

        int TEST_RETRIES = 5;
        int MINIMAL_MAX_BLOCK_MILLISECONDS = 10;

        config = new KafkaProducerConfig();
        config.setBrokerAddress(bootstrapServers);
        config.setAcks(Acks.NO_RESPONSE);
        config.setRetries(TEST_RETRIES);
        config.setMaxBlockMilliseconds(MINIMAL_MAX_BLOCK_MILLISECONDS);
        kafkaTemplate = config.kafkaTemplate();
        notificationProducer = new NotificationProducer(EMAIL_TOPIC,
            LETTER_TOPIC,
            config,
            kafkaTemplate);
    }

    @Test
    public void emailShouldTimeout() {
        try {
            notificationProducer.sendEmail(myByte);
        } catch (NotificationSendingException e) {
            Assertions.assertInstanceOf(TimeoutException.class, e.getCause());
            return;
        }
        Assertions.fail("A timeout exception should have been thrown");
    }

    @Test
    public void letterShouldTimeout() {
        try {
            notificationProducer.sendLetter(myByte);
        } catch (NotificationSendingException e) {
            Assertions.assertInstanceOf(TimeoutException.class, e.getCause());
            return;
        }
        Assertions.fail("A timeout exception should have been thrown");
    }

}
