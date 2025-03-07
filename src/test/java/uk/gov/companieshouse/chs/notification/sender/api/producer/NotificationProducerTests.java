package uk.gov.companieshouse.chs.notification.sender.api.producer;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Properties;
import java.util.concurrent.Future;

class NotificationProducerTests {

    private static final int TEST_RETRIES = 5;
    private static final String TEST_BROKER = "test-broker";
    private static final int MAX_BLOCK_MILLISECONDS = 1000;
    private static final int REQUEST_TIMEOUT_MILLISECONDS = 1000;

    private static final String EMAIL_TOPIC = "${kafka.topic.email}";
    private static final String LETTER_TOPIC = "${kafka.topic.letter}";
    private final byte[] myByte = new byte[]{0x1};

    private NotificationProducer producer;

    @Mock
    private KafkaProducer<String, byte[]> mockKafkaProducer;
    @Mock
    private Future<RecordMetadata> recordMetadataFuture;

    @Mock
    private KafkaProducerFactory mockProducerFactory;

    @BeforeEach
    public void test() {
        MockitoAnnotations.openMocks(this);

        when(mockProducerFactory.getProducer(any(Properties.class))).thenReturn(mockKafkaProducer);
        given(mockKafkaProducer.send(ArgumentMatchers.<ProducerRecord<String,byte[]>>any())).willReturn(recordMetadataFuture);
    }

    @Test
    public void testSendAndReturnFutureEmail() throws Exception {
        createTestProducer(true, Acks.NO_RESPONSE, true);
        producer.sendEmail(myByte, EMAIL_TOPIC);

        verify(mockKafkaProducer).send(ArgumentMatchers.<ProducerRecord<String,byte[]>>any());
    }
    @Test
    public void testSendAndReturnFutureLetter() throws Exception {
        createTestProducer(true, Acks.NO_RESPONSE, true);
        producer.sendEmail(myByte, LETTER_TOPIC);

        verify(mockKafkaProducer).send(ArgumentMatchers.any());
    }

    @Test
    public void testSendAndReturnFutureEmailWithoutIdempotence() throws Exception {
        createTestProducer(true, Acks.NO_RESPONSE, false);
        producer.sendAndReturnFuture(EMAIL_TOPIC, myByte);

        verify(mockKafkaProducer).send(ArgumentMatchers.<ProducerRecord<String,byte[]>>any());
    }
    @Test
    public void testSendAndReturnFutureLetterWithoutIdempotence() throws Exception {
        createTestProducer(true, Acks.NO_RESPONSE, false);
        producer.sendAndReturnFutureLetter(EMAIL_TOPIC, myByte);

        verify(mockKafkaProducer).send(ArgumentMatchers.<ProducerRecord<String,byte[]>>any());
    }


    @Test
    @SuppressWarnings("unchecked")
    public void testSendRoundRobinAcksNoResponse() throws Exception {
        createTestProducer(true, Acks.NO_RESPONSE, true);
        producer.sendEmail(myByte, EMAIL_TOPIC);
        verify(mockKafkaProducer).send(any(ProducerRecord.class));
        verify(recordMetadataFuture, times(1)).get();
    }
    @Test
    @SuppressWarnings("unchecked")
    public void testSendRoundRobinAcksNoResponseForLetter() throws Exception {
        createTestProducer(true, Acks.NO_RESPONSE, true);
        producer.sendLetter(myByte, LETTER_TOPIC);
        verify(mockKafkaProducer).send(any(ProducerRecord.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSendManualPartitionAcksNoResponse() throws Exception {
        createTestProducer(false, Acks.NO_RESPONSE, true);
        producer.sendEmail(myByte, EMAIL_TOPIC);
        verify(mockKafkaProducer).send(any(ProducerRecord.class));
        verify(recordMetadataFuture, times(1)).get();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSendRoundRobinAcksWaitForLocal() throws Exception {
        createTestProducer(true, Acks.WAIT_FOR_LOCAL, true);
        producer.sendEmail(myByte, EMAIL_TOPIC);
        verify(mockKafkaProducer).send(any(ProducerRecord.class));
        verify(recordMetadataFuture, times(1)).get();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSendRoundRobinAcksWaitForAll() throws Exception {
        createTestProducer(true, Acks.WAIT_FOR_ALL, true);
        producer.sendEmail(myByte, EMAIL_TOPIC);
        verify(mockKafkaProducer).send(any(ProducerRecord.class));
        verify(recordMetadataFuture, times(1)).get();
    }

    @Test
    public void testCloseRoundRobinAcksNoResponse() throws Exception {
        createTestProducer(true, Acks.NO_RESPONSE, true);
        producer.close();
        verify(mockKafkaProducer).close();
    }


    /**
     * Create the test configuration and a producer to test
     *
     */
    private void createTestProducer(boolean roundRobinPartitioner, Acks acks, boolean idempotence) throws Exception {
        ProducerConfig config = new ProducerConfig();
        config.setAcks(acks);
        config.setBrokerAddresses(new String[]{TEST_BROKER});
        config.setRetries(TEST_RETRIES);
        config.setRoundRobinPartitioner(roundRobinPartitioner);
        config.setEnableIdempotence(idempotence);

        producer = new NotificationProducer(config, mockProducerFactory);

        Properties expectedProperties = expectedKafkaProducerConfigProperties(roundRobinPartitioner, acks.getCode(), idempotence);
        verify(mockProducerFactory, times(1)).getProducer(expectedProperties);
    }
    /**
     * Create expected properties so we can confirm the actual properties used to create
     * the producer match the ProducerConfig we supply
     *
     */
    private Properties expectedKafkaProducerConfigProperties(boolean roundRobinPartitioner, String acksCode, boolean idempotence) throws Exception {
        Properties props = new Properties();
        props.put("bootstrap.servers", String.join(",", new String[]{TEST_BROKER}));
        props.put("acks", acksCode);
        props.put("retries", TEST_RETRIES);
        props.put("value.serializer","org.apache.kafka.common.serialization.ByteArraySerializer");
        props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        props.put("max.block.ms", MAX_BLOCK_MILLISECONDS);
        props.put("request.timeout.ms", REQUEST_TIMEOUT_MILLISECONDS);
        props.put("enable.idempotence", idempotence);

        if(roundRobinPartitioner) {
            props.put("partition.assignment.strategy", "roundrobin");
        }
        return props;
    }
}
