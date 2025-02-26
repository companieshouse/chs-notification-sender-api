package uk.gov.companieshouse.chs.notification.sender.api.restapi;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkLetterDetailsRequest;
import uk.gov.companieshouse.chs.notification.sender.api.translator.KafkaTranslatorInterface;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit-test")
public class NotificationServiceTest {

    @Mock
    private KafkaTranslatorInterface kafkaMessageTranslator;

    @InjectMocks
    private NotificationService notificationService;


    @Test
    @DisplayName(" Translate Email Notification")
    void shouldTranslateEmailNotificationSuccessfully() {
        GovUkEmailDetailsRequest emailRequest = new GovUkEmailDetailsRequest();
        byte[] expectedBytes = new byte[]{1, 2, 3};

        when(kafkaMessageTranslator.translateNotificationToEmailKafkaMessage(emailRequest)).thenReturn(expectedBytes);

        byte[] result = notificationService.translateEmailNotification(emailRequest);

        assertArrayEquals(expectedBytes, result);
        verify(kafkaMessageTranslator, times(1)).translateNotificationToEmailKafkaMessage(emailRequest);
    }

    @SneakyThrows
    @Test
    @DisplayName(" Translate Letter Notification")
    void shouldTranslateLetterNotificationSuccessfully() {
        GovUkLetterDetailsRequest letterRequest = new GovUkLetterDetailsRequest();
        byte[] expectedBytes = new byte[]{4, 5, 6};

        when(kafkaMessageTranslator.translateNotificationToLetterKafkaMessage(letterRequest)).thenReturn(expectedBytes);

        byte[] result = notificationService.translateLetterNotification(letterRequest);

        assertArrayEquals(expectedBytes, result);
        verify(kafkaMessageTranslator, times(1)).translateNotificationToLetterKafkaMessage(letterRequest);
    }

    @Test
    void testTranslateEmailNotificationThrowsException() {
        GovUkEmailDetailsRequest emailRequest = new GovUkEmailDetailsRequest();

        when(kafkaMessageTranslator.translateNotificationToEmailKafkaMessage(emailRequest)).thenThrow(new RuntimeException("Kafka translation error"));

        assertThrows(RuntimeException.class, () -> notificationService.translateEmailNotification(emailRequest));
        verify(kafkaMessageTranslator, times(1)).translateNotificationToEmailKafkaMessage(emailRequest);
    }

}
