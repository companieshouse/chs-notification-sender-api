package uk.gov.companieshouse.chs.notification.sender.api.producer;

import org.apache.kafka.common.errors.TimeoutException;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class NotificationProducerIntegrationTests {

    /**
     * The object under test
     */
    private NotificationProducer producer;

    private static final String EMAIL_TOPIC = "${kafka.topic.email}";
    private static final String LETTER_TOPIC = "${kafka.topic.letter}";
    private final byte[] myByte = new byte[]{0x1};

    @Before
    public void setUp() {
        ProducerConfig producerConfig = createProducerConfig();
        producer = new NotificationProducer(producerConfig);
    }

    @Test
    public void shouldTimeoutEmail() throws ExecutionException, InterruptedException {
        try {
            producer.sendEmail(myByte,EMAIL_TOPIC);
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof TimeoutException);
            e.getCause().printStackTrace();
            return;
        }
        fail("A timeout exception should have been thrown");
    }
    @Test
    public void shouldTimeoutLetter() throws ExecutionException, InterruptedException {
        try {
            producer.sendEmail(myByte,LETTER_TOPIC);
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof TimeoutException);
            e.getCause().printStackTrace();
            return;
        }
        fail("A timeout exception should have been thrown");
    }

    /**
     * Create a test {@link ProducerConfig}
     *
     * @return A {@link ProducerConfig}
     */
    private ProducerConfig createProducerConfig() {
        ProducerConfig config = new ProducerConfig();
        config.setBrokerAddresses(new String[] {"localhost:1234"});
        config.setRoundRobinPartitioner(true);
        config.setAcks(Acks.NO_RESPONSE);
        config.setRetries(10);
        config.setMaxBlockMilliseconds(10);
        config.setEnableIdempotence(false);
        return config;
    }
}
