package uk.gov.companieshouse.chs.notification.sender.api.kafka;

import static org.assertj.core.api.Assertions.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

@ExtendWith(MockitoExtension.class)
@Tag("unit-test")
class NotificationProducerTests {

    private static final String EMAIL_TOPIC = "chs-notification-email";
    private static final String LETTER_TOPIC = "chs-notification-letter";

    private final byte[] myByte = new byte[]{0x1};

    private final SendResult<String, byte[]> result = new SendResult<>(
        new ProducerRecord<String, byte[]>(EMAIL_TOPIC, myByte),
        null);
    @Mock
    private KafkaProducerConfig kafkaProducerConfig;

    @Mock
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    @InjectMocks
    private NotificationProducer producer;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        producer = new NotificationProducer(EMAIL_TOPIC, LETTER_TOPIC, kafkaProducerConfig,
            kafkaTemplate);
    }

    @Test
    void testDataIsSentToKafkaTemplateWhenSendEmailSucceeds()
        throws NotificationSendingException, ExecutionException, InterruptedException {
        ProducerRecord<String, byte[]> producerRecord = new ProducerRecord<>(EMAIL_TOPIC, myByte);
        given(
            kafkaTemplate.send(ArgumentMatchers.<ProducerRecord<String, byte[]>>any())).willReturn(
            CompletableFuture.completedFuture(result));

        Assertions.assertDoesNotThrow(() -> producer.sendEmail(myByte));
        verify(kafkaTemplate, times(1)).send(producerRecord);
    }

    @Test
    void testExceptionWhenEmailFailedSendingDueToTimeOut()
        throws NotificationSendingException, ExecutionException, InterruptedException {
        given(kafkaTemplate.send(ArgumentMatchers.<ProducerRecord<String, byte[]>>any())).willThrow(
            new org.apache.kafka.common.errors.TimeoutException(
                "Expiring 1 record(s) for chs-notification-email-0"));

        Exception exception = Assertions.assertThrows(NotificationSendingException.class,
            () -> producer.sendEmail(myByte));
        Assertions.assertTrue(
            exception.getMessage().contains("Expiring 1 record(s) for chs-notification-email-0"));
    }

    @Test
    void testExceptionWhenSendingEmailFailedDueToMessageTooLarge()
        throws NotificationSendingException, ExecutionException, InterruptedException {
        given(kafkaTemplate.send(ArgumentMatchers.<ProducerRecord<String, byte[]>>any())).willThrow(
            new KafkaException("Failed to Send",
                new Throwable("""
                    Unable to send chs-notification-email The request
                    included a message larger than the max message size
                    that the server will accept
                    """)));

        Exception exception = Assertions.assertThrows(NotificationSendingException.class,
            () -> producer.sendEmail(myByte));
        Assertions.assertTrue(
            exception.getMessage().contains("included a message larger than the max message size"));
    }

    @Test
    void testDataIsSentToKafkaTemplateWhenSendLetterSucceeds()
        throws NotificationSendingException, ExecutionException, InterruptedException {
        ProducerRecord<String, byte[]> producerRecord = new ProducerRecord<>(LETTER_TOPIC, myByte);
        given(
            kafkaTemplate.send(ArgumentMatchers.<ProducerRecord<String, byte[]>>any())).willReturn(
            CompletableFuture.completedFuture(result));

        Assertions.assertDoesNotThrow(() -> producer.sendLetter(myByte));
        verify(kafkaTemplate, times(1)).send(producerRecord);
    }

    @Test
    void testExceptionWhenLetterFailedSendingDueToTimeOut()
        throws NotificationSendingException, ExecutionException, InterruptedException {
        given(kafkaTemplate.send(ArgumentMatchers.<ProducerRecord<String, byte[]>>any())).willThrow(
            new org.apache.kafka.common.errors.TimeoutException(
                "Expiring 1 record(s) for chs-notification-letter-0"));

        Exception exception = Assertions.assertThrows(NotificationSendingException.class,
            () -> producer.sendLetter(myByte));
        Assertions.assertTrue(
            exception.getMessage().contains("Expiring 1 record(s) for chs-notification-letter-0"));
    }

    @Test
    void testExceptionWhenSendingLetterFailedDueToMessageTooLarge()
        throws NotificationSendingException, ExecutionException, InterruptedException {
        given(kafkaTemplate.send(ArgumentMatchers.<ProducerRecord<String, byte[]>>any())).willThrow(
            new KafkaException("Failed to Send",
                new Throwable("""
                    Unable to send chs-notification-letter The request
                    included a message larger than the max message size
                    that the server will accept
                    """
                )));

        Exception exception = Assertions.assertThrows(NotificationSendingException.class,
            () -> producer.sendLetter(myByte));
        Assertions.assertTrue(
            exception.getMessage().contains(
                "included a message larger than the max message size"));
    }

    @Test
    void testInterruptedExceptionWhenSendingEmail() {

        Thread taskThread = new Thread(() -> {
            producer.sendEmail(myByte);
        });

        taskThread.start();
        taskThread.interrupt();
        try {
            taskThread.join();
        } catch (InterruptedException e) {
            fail("TestThread Was interrupted");
        }
        Assertions.assertTrue(taskThread.isInterrupted());
    }


}

