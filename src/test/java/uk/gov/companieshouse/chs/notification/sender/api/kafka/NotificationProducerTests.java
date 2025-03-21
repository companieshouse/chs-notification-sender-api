package uk.gov.companieshouse.chs.notification.sender.api.kafka;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

@ExtendWith(MockitoExtension.class)
@Tag("unit-test")
public class NotificationProducerTests {

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

    @Captor
    private ArgumentCaptor<ProducerRecord<String, byte[]>> captor;

    @BeforeEach
    public void test() {
        MockitoAnnotations.openMocks(this);
        producer = new NotificationProducer(EMAIL_TOPIC, LETTER_TOPIC, kafkaProducerConfig,
            kafkaTemplate);
    }

    @Test
    public void sendEmailSucceeds()
        throws NotificationSendingException, ExecutionException, InterruptedException {
        given(
            kafkaTemplate.send(ArgumentMatchers.<ProducerRecord<String, byte[]>>any())).willReturn(
            CompletableFuture.completedFuture(result));

        Assertions.assertDoesNotThrow(() -> producer.sendEmail(myByte));
        verify(kafkaTemplate).send(captor.capture());
    }

    @Test
    public void sendEmailFailedToSendTimeOut()
        throws NotificationSendingException, ExecutionException, InterruptedException {
        given(kafkaTemplate.send(ArgumentMatchers.<ProducerRecord<String, byte[]>>any())).willThrow(
            new NotificationSendingException("Unable to send chs-notification-email Expiring 1 "
                + "record(s) for chs-notification-email-0:120001 ms has passed since batch "
                + "creation",
                new Throwable("Failed to Send")));

        Exception exception = Assertions.assertThrows(NotificationSendingException.class,
            () -> producer.sendEmail(myByte));
        Assertions.assertTrue(
            exception.getMessage().contains("Expiring 1 record(s) for chs-notification-email-0"));
    }

    @Test
    public void sendEmailFailedToSendTooLarge()
        throws NotificationSendingException, ExecutionException, InterruptedException {
        given(kafkaTemplate.send(ArgumentMatchers.<ProducerRecord<String, byte[]>>any())).willThrow(
            new NotificationSendingException("Unable to send chs-notification-email The request "
                + "included a message larger than the max message size the server will accept",
                new Throwable("Failed to Send")));

        Exception exception = Assertions.assertThrows(NotificationSendingException.class,
            () -> producer.sendEmail(myByte));
        Assertions.assertTrue(exception.getMessage().contains("The request "
            + "included a message larger than the max message "));
    }

    @Test
    public void sendLetterSucceeds()
        throws NotificationSendingException, ExecutionException, InterruptedException {
        given(
            kafkaTemplate.send(ArgumentMatchers.<ProducerRecord<String, byte[]>>any())).willReturn(
            CompletableFuture.completedFuture(result));

        Assertions.assertDoesNotThrow(() -> producer.sendLetter(myByte));
        verify(kafkaTemplate).send(captor.capture());
    }

    @Test
    public void sendLetterFailedToSendTimeOut()
        throws NotificationSendingException, ExecutionException, InterruptedException {
        given(kafkaTemplate.send(ArgumentMatchers.<ProducerRecord<String, byte[]>>any())).willThrow(
            new NotificationSendingException(
                "Unable to send chs-notification-email Expiring 1 record(s) for "
                    + "chs-notification-letter-0:120001 ms has passed since batch creation",
                new Throwable("Failed to Send")));

        Exception exception = Assertions.assertThrows(NotificationSendingException.class,
            () -> producer.sendLetter(myByte));
        Assertions.assertTrue(
            exception.getMessage().contains("Expiring 1 record(s) for chs-notification-letter-0"));
    }

    @Test
    public void sendLetterFailedToSendTooLarge()
        throws NotificationSendingException, ExecutionException, InterruptedException {
        given(kafkaTemplate.send(ArgumentMatchers.<ProducerRecord<String, byte[]>>any())).willThrow(
            new NotificationSendingException("Unable to send chs-notification-letter The request "
                + "included a message larger than the max message size the server will accept",
                new Throwable("Failed to Send")));

        Exception exception = Assertions.assertThrows(NotificationSendingException.class,
            () -> producer.sendLetter(myByte));
        Assertions.assertTrue(
            exception.getMessage().contains("The request "
                + "included a message larger than the max message "));


    }
}
