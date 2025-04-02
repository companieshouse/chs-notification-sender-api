package uk.gov.companieshouse.chs.notification.sender.api.restapi;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkLetterDetailsRequest;
import uk.gov.companieshouse.chs.notification.sender.api.kafka.KafkaProducerInterface;
import uk.gov.companieshouse.chs.notification.sender.api.kafka.NotificationSendingException;
import uk.gov.companieshouse.chs.notification.sender.api.translator.KafkaTranslatorInterface;

@ExtendWith(MockitoExtension.class)
@Tag("unit-test")
public class NotificationServiceTest {

    private final byte[] myByte = new byte[]{0x1};

    @Mock
    private KafkaTranslatorInterface kafkaMessageTranslator;

    @Mock
    private KafkaProducerInterface notificationProducer;

    @InjectMocks
    private NotificationService notificationService;


    @Test
    @DisplayName(" Translate Email Notification")
    void shouldTranslateEmailNotificationSuccessfully() {
        GovUkEmailDetailsRequest emailRequest = new GovUkEmailDetailsRequest();
        byte[] expectedBytes = new byte[]{1, 2, 3};

        when(kafkaMessageTranslator.translateNotificationToEmailKafkaMessage(
            emailRequest)).thenReturn(expectedBytes);

        byte[] result = notificationService.translateEmailNotification(emailRequest);

        assertArrayEquals(expectedBytes, result);
        verify(kafkaMessageTranslator, times(1)).translateNotificationToEmailKafkaMessage(
            emailRequest);
    }

    @Test
    @DisplayName(" Translate Letter Notification")
    void shouldTranslateLetterNotificationSuccessfully() {
        GovUkLetterDetailsRequest letterRequest = new GovUkLetterDetailsRequest();
        byte[] expectedBytes = new byte[]{4, 5, 6};

        when(kafkaMessageTranslator.translateNotificationToLetterKafkaMessage(
            letterRequest)).thenReturn(expectedBytes);

        byte[] result = notificationService.translateLetterNotification(letterRequest);

        assertArrayEquals(expectedBytes, result);
        verify(kafkaMessageTranslator, times(1)).translateNotificationToLetterKafkaMessage(
            letterRequest);
    }

    @Test
    void testTranslateEmailNotificationThrowsException() {
        GovUkEmailDetailsRequest emailRequest = new GovUkEmailDetailsRequest();

        when(kafkaMessageTranslator.translateNotificationToEmailKafkaMessage(
            emailRequest)).thenThrow(new RuntimeException("Kafka translation error"));

        assertThrows(RuntimeException.class,
            () -> notificationService.translateEmailNotification(emailRequest));
        verify(kafkaMessageTranslator, times(1)).translateNotificationToEmailKafkaMessage(
            emailRequest);
    }

    @Test
    void shouldSendEmailSuccessfully() {
        notificationService.sendEmail(myByte);
        verify(notificationProducer, times(1)).sendEmail(myByte);
    }

    @Test
    void shouldSendLetterSuccessfully() {
        notificationService.sendLetter(myByte);
        verify(notificationProducer, times(1)).sendLetter(myByte);
    }

    @Test
    void testSendEmailThrowsException() {
        doThrow(new NotificationSendingException(
            "kafka exception", new Throwable()))
            .when(notificationProducer).sendEmail(myByte);

        assertThrows(NotificationSendingException.class,
            () -> notificationService.sendEmail(myByte));

        verify(notificationProducer, times(1)).sendEmail(myByte);
    }

    @Test
    void testSendLetterThrowsException() {
        doThrow(new NotificationSendingException(
            "kafka exception", new Throwable()))
            .when(notificationProducer).sendLetter(myByte);

        assertThrows(NotificationSendingException.class,
            () -> notificationService.sendLetter(myByte));

        verify(notificationProducer, times(1)).sendLetter(myByte);
    }
}