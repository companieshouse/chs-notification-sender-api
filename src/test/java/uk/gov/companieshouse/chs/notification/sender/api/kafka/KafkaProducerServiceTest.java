package uk.gov.companieshouse.chs.notification.sender.api.kafka;

import static helpers.utils.OutputAssertions.assertJsonHasAndEquals;
import static helpers.utils.OutputAssertions.getDataFromLogMessage;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.chs.notification.sender.api.TestUtil.createValidEmailRequest;
import static uk.gov.companieshouse.chs.notification.sender.api.TestUtil.createValidLetterRequest;

import helpers.OutputCapture;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.chs.notification.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.api.chs.notification.model.GovUkLetterDetailsRequest;
import uk.gov.companieshouse.chs.notification.sender.api.config.ApplicationConfig;
import uk.gov.companieshouse.chs.notification.sender.api.exception.NotificationException;
import uk.gov.companieshouse.logging.EventType;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private ApplicationConfig applicationConfig;


    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(kafkaProducerService, "kafkaTemplate", kafkaTemplate);
    }

    @Test
    void When_SendEmailWithValidRequest_Expect_SuccessfulDelivery() {
        CompletableFuture<SendResult<String, byte[]>> future = CompletableFuture.completedFuture(
            new SendResult<>(new ProducerRecord<>(applicationConfig.getEmailTopic(), new byte[0]),
                null)
        );
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);

        kafkaProducerService.sendEmail(createValidEmailRequest(), "TODO DEEP-490");

        verify(kafkaTemplate).send(any(ProducerRecord.class));
    }

    @Test
    void When_SendEmailWithValidRequest_Expect_DebugLogMessage() throws IOException {
        CompletableFuture<SendResult<String, byte[]>> future = CompletableFuture.completedFuture(
                new SendResult<>(new ProducerRecord<>(applicationConfig.getEmailTopic(), new byte[0]),
                        null)
        );
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);

        try(var outputCapture = new OutputCapture()) {
            kafkaProducerService.sendEmail(createValidEmailRequest(), "TODO DEEP-490");
            var debugData = getDataFromLogMessage(outputCapture, EventType.DEBUG,
                    "Sending message to topic: chs-notification-email");
            assertJsonHasAndEquals(debugData, "topic", applicationConfig.getEmailTopic());
        }
    }

    @Test
    void When_SendLetterWithValidRequest_Expect_SuccessfulDelivery() {
        CompletableFuture<SendResult<String, byte[]>> future = CompletableFuture.completedFuture(
            new SendResult<>(new ProducerRecord<>(applicationConfig.getLetterTopic(), new byte[0]),
                null)
        );
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);

        kafkaProducerService.sendLetter(createValidLetterRequest(), "TODO DEEP-490");

        verify(kafkaTemplate).send(any(ProducerRecord.class));
    }

    @Test
    void When_KafkaSendThrowsInterruptedException_Expect_NotificationException() {
        CompletableFuture<SendResult<String, byte[]>> future = new CompletableFuture<>();
        future.completeExceptionally(new InterruptedException("Test interruption"));
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);
        GovUkEmailDetailsRequest emailRequest = createValidEmailRequest();
        assertThrows(NotificationException.class,
            () -> kafkaProducerService.sendEmail(emailRequest, "TODO DEEP-490"));
    }

    @Test
    void When_KafkaSendThrowsExecutionException_Expect_NotificationException() {
        CompletableFuture<SendResult<String, byte[]>> future = new CompletableFuture<>();
        future.completeExceptionally(
            new ExecutionException(new RuntimeException("Test execution error")));
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);

        GovUkEmailDetailsRequest emailRequest = createValidEmailRequest();
        assertThrows(NotificationException.class,
            () -> kafkaProducerService.sendEmail(emailRequest, "TODO DEEP-490"));
    }

    @Test
    void When_KafkaSendThrowsTimeoutException_Expect_NotificationException() {
        CompletableFuture<SendResult<String, byte[]>> future = new CompletableFuture<>();
        future.completeExceptionally(new TimeoutException("Test timeout"));
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);
        GovUkEmailDetailsRequest emailRequest = createValidEmailRequest();
        assertThrows(NotificationException.class,
            () -> kafkaProducerService.sendEmail(emailRequest, "TODO DEEP-490"));
    }

    @Test
    void When_SendLetterWithTimeoutException_Expect_NotificationException() {
        CompletableFuture<SendResult<String, byte[]>> future = new CompletableFuture<>();
        future.completeExceptionally(new TimeoutException("Test timeout"));
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);
        GovUkLetterDetailsRequest letterDetailsRequest = createValidLetterRequest();
        assertThrows(NotificationException.class,
            () -> kafkaProducerService.sendLetter(letterDetailsRequest, "TODO DEEP-490"));
    }

    @Test
    void When_SendLetterWithExecutionException_Expect_NotificationException() {
        CompletableFuture<SendResult<String, byte[]>> future = new CompletableFuture<>();
        future.completeExceptionally(
            new ExecutionException(new RuntimeException("Test execution error")));
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);

        GovUkLetterDetailsRequest letterDetailsRequest = createValidLetterRequest();
        assertThrows(NotificationException.class,
            () -> kafkaProducerService.sendLetter(letterDetailsRequest, "TODO DEEP-490"));
    }

    @Test
    void When_SendLetterWithInterruptedException_Expect_ThreadInterrupted() {
        CompletableFuture<SendResult<String, byte[]>> future = new CompletableFuture<>();
        future.completeExceptionally(new InterruptedException("Test interruption"));
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);

        GovUkLetterDetailsRequest letterDetailsRequest = createValidLetterRequest();
        assertThrows(NotificationException.class,
            () -> kafkaProducerService.sendLetter(letterDetailsRequest, "TODO DEEP-490"));
        assertTrue(Thread.currentThread().isInterrupted(), "Thread should be interrupted");
    }

}
