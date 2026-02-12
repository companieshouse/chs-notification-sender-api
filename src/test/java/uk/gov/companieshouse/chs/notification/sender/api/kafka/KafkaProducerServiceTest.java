package uk.gov.companieshouse.chs.notification.sender.api.kafka;

import static com.google.common.net.HttpHeaders.X_REQUEST_ID;
import static helpers.utils.OutputAssertions.assertJsonHasAndEquals;
import static helpers.utils.OutputAssertions.getDataFromLogMessage;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
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
import org.apache.kafka.common.header.Headers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import uk.gov.companieshouse.chs.notification.sender.api.mapper.NotificationMapper;
import uk.gov.companieshouse.logging.EventType;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class KafkaProducerServiceTest {

    private static class TestKafkaProducerService extends KafkaProducerService {
        private final ProducerRecord<String, byte[]> producerRecord;

        public TestKafkaProducerService(final KafkaTemplate<String, byte[]> kafkaTemplate,
                                        final NotificationMapper notificationMapper,
                                        final ApplicationConfig applicationConfig,
                                        final ProducerRecord<String, byte[]> producerRecord) {
            super(kafkaTemplate, notificationMapper, applicationConfig);
            this.producerRecord = producerRecord;
        }

        @Override
        protected ProducerRecord<String, byte[]> createProducerRecord(final String topic,
                                                                      final byte[] message) {
            return producerRecord;
        }

    }

    @Mock
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    @Mock
    private ProducerRecord<String, byte[]> producerRecord;

    @Mock
    private Headers headers;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired NotificationMapper notificationMapper;

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

        kafkaProducerService.sendEmail(createValidEmailRequest(), "test-request-id");

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
            kafkaProducerService.sendEmail(createValidEmailRequest(), "test-request-id");
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

        kafkaProducerService.sendLetter(createValidLetterRequest(), "test-request-id");

        verify(kafkaTemplate).send(any(ProducerRecord.class));
    }

    @Test
    void When_KafkaSendThrowsInterruptedException_Expect_NotificationException() {
        CompletableFuture<SendResult<String, byte[]>> future = new CompletableFuture<>();
        future.completeExceptionally(new InterruptedException("Test interruption"));
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);
        GovUkEmailDetailsRequest emailRequest = createValidEmailRequest();
        assertThrows(NotificationException.class,
            () -> kafkaProducerService.sendEmail(emailRequest, "test-request-id"));
    }

    @Test
    void When_KafkaSendThrowsExecutionException_Expect_NotificationException() {
        CompletableFuture<SendResult<String, byte[]>> future = new CompletableFuture<>();
        future.completeExceptionally(
            new ExecutionException(new RuntimeException("Test execution error")));
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);

        GovUkEmailDetailsRequest emailRequest = createValidEmailRequest();
        assertThrows(NotificationException.class,
            () -> kafkaProducerService.sendEmail(emailRequest, "test-request-id"));
    }

    @Test
    void When_KafkaSendThrowsTimeoutException_Expect_NotificationException() {
        CompletableFuture<SendResult<String, byte[]>> future = new CompletableFuture<>();
        future.completeExceptionally(new TimeoutException("Test timeout"));
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);
        GovUkEmailDetailsRequest emailRequest = createValidEmailRequest();
        assertThrows(NotificationException.class,
            () -> kafkaProducerService.sendEmail(emailRequest, "test-request-id"));
    }

    @Test
    void When_SendLetterWithTimeoutException_Expect_NotificationException() {
        CompletableFuture<SendResult<String, byte[]>> future = new CompletableFuture<>();
        future.completeExceptionally(new TimeoutException("Test timeout"));
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);
        GovUkLetterDetailsRequest letterDetailsRequest = createValidLetterRequest();
        assertThrows(NotificationException.class,
            () -> kafkaProducerService.sendLetter(letterDetailsRequest, "test-request-id"));
    }

    @Test
    void When_SendLetterWithExecutionException_Expect_NotificationException() {
        CompletableFuture<SendResult<String, byte[]>> future = new CompletableFuture<>();
        future.completeExceptionally(
            new ExecutionException(new RuntimeException("Test execution error")));
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);

        GovUkLetterDetailsRequest letterDetailsRequest = createValidLetterRequest();
        assertThrows(NotificationException.class,
            () -> kafkaProducerService.sendLetter(letterDetailsRequest, "test-request-id"));
    }

    @Test
    void When_SendLetterWithInterruptedException_Expect_ThreadInterrupted() {
        CompletableFuture<SendResult<String, byte[]>> future = new CompletableFuture<>();
        future.completeExceptionally(new InterruptedException("Test interruption"));
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);

        GovUkLetterDetailsRequest letterDetailsRequest = createValidLetterRequest();
        assertThrows(NotificationException.class,
            () -> kafkaProducerService.sendLetter(letterDetailsRequest, "test-request-id"));
        assertTrue(Thread.currentThread().isInterrupted(), "Thread should be interrupted");
    }

    @Test
    @DisplayName("A non-null context ID is propagated through the 'X-Request-ID' Kafka header")
    void nonNullContextIdIsPropagated() {
        var producerService =
                new TestKafkaProducerService(kafkaTemplate,
                                             notificationMapper,
                                             applicationConfig,
                                             producerRecord);
        CompletableFuture<SendResult<String, byte[]>> future = CompletableFuture.completedFuture(
                new SendResult<>(new ProducerRecord<>(applicationConfig.getEmailTopic(), new byte[0]),
                        null)
        );
        when(producerRecord.headers()).thenReturn(headers);
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);

        producerService.sendEmail(createValidEmailRequest(), "test-request-id");

        verify(kafkaTemplate).send(any(ProducerRecord.class));
        verify(headers).add(X_REQUEST_ID, "test-request-id".getBytes());
    }

    @Test
    @DisplayName("A null context ID is NOT propagated through the 'X-Request-ID' Kafka header")
    void nullContextIdIsNotPropagated() {
        var producerService =
                new TestKafkaProducerService(kafkaTemplate,
                        notificationMapper,
                        applicationConfig,
                        producerRecord);
        CompletableFuture<SendResult<String, byte[]>> future = CompletableFuture.completedFuture(
                new SendResult<>(new ProducerRecord<>(applicationConfig.getEmailTopic(), new byte[0]),
                        null)
        );
        when(producerRecord.headers()).thenReturn(headers);
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);

        producerService.sendEmail(createValidEmailRequest(), null);

        verify(kafkaTemplate).send(any(ProducerRecord.class));
        verify(headers, never()).add(eq(X_REQUEST_ID), any(byte[].class));
    }

}
