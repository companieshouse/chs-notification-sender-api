package uk.gov.companieshouse.chs.notification.sender.api.restapi;

import com.fasterxml.jackson.core.JsonProcessingException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit-test")
public class EmailLetterServiceTest {

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private KafkaTranslatorInterface kafkaMessageTranslator;

    @Mock
    private EmailDetailsRequest emailDetailsRequest;

    @Mock
    private LetterDetailsRequest letterDetailsRequest;

    @InjectMocks
    private EmailLetterService emailLetterService;

    private final GovUkEmailDetailsRequest emailRequest = mock(GovUkEmailDetailsRequest.class);
    private final GovUkLetterDetailsRequest letterRequest = mock(GovUkLetterDetailsRequest.class);

    @SneakyThrows
    @Test
    @DisplayName(" Translate Email Notification")
    void shouldTranslateEmailNotificationSuccessfully() {
        when(notificationMapper.mapToEmailDetailsRequest(emailRequest)).thenReturn(emailDetailsRequest);
        when(emailDetailsRequest.convertToJson()).thenReturn("{\"email\":\"test@example.com\"}");

        byte[] expectedBytes = "emailMessage".getBytes();
        when(kafkaMessageTranslator.translateNotificationToEmailKafkaMessage(anyString())).thenReturn(expectedBytes);

        byte[] result = emailLetterService.translateEmailNotification(emailRequest);

        assertArrayEquals(expectedBytes, result);
        verify(notificationMapper).mapToEmailDetailsRequest(emailRequest);
        verify(kafkaMessageTranslator).translateNotificationToEmailKafkaMessage("{\"email\":\"test@example.com\"}");
    }

    @SneakyThrows
    @Test
    @DisplayName(" Translate Letter Notification")
    void shouldTranslateLetterNotificationSuccessfully() {
        when(notificationMapper.mapToLetterDetailsRequest(letterRequest)).thenReturn(letterDetailsRequest);
        when(letterDetailsRequest.convertToJson()).thenReturn("{\"letter\":\"test\"}");

        byte[] expectedBytes = "letterMessage".getBytes();
        when(kafkaMessageTranslator.translateNotificationToLetterKafkaMessage(anyString())).thenReturn(expectedBytes);

        byte[] result = emailLetterService.translateLetterNotification(letterRequest);

        assertArrayEquals(expectedBytes, result);
        verify(notificationMapper).mapToLetterDetailsRequest(letterRequest);
        verify(kafkaMessageTranslator).translateNotificationToLetterKafkaMessage("{\"letter\":\"test\"}");
    }

    @Test
    @DisplayName(" Exception when email conversion fails")
    void shouldThrowExceptionWhenEmailJsonConversionFails() throws JsonProcessingException {
        when(notificationMapper.mapToEmailDetailsRequest(emailRequest)).thenReturn(emailDetailsRequest);
        when(emailDetailsRequest.convertToJson()).thenThrow(new JsonProcessingException("Error") {});

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> emailLetterService.translateEmailNotification(emailRequest));

        assertEquals("Invalid message format", exception.getMessage());
        verify(notificationMapper).mapToEmailDetailsRequest(emailRequest);
        verify(emailDetailsRequest).convertToJson();
    }

    @Test
    @DisplayName(" Exception when letter conversion fails")
    void shouldThrowExceptionWhenLetterJsonConversionFails() throws JsonProcessingException {
        when(notificationMapper.mapToLetterDetailsRequest(letterRequest)).thenReturn(letterDetailsRequest);
        when(letterDetailsRequest.convertToJson()).thenThrow(new JsonProcessingException("Error") {});

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> emailLetterService.translateLetterNotification(letterRequest));

        assertEquals("Invalid message format", exception.getMessage());
        verify(notificationMapper).mapToLetterDetailsRequest(letterRequest);
        verify(letterDetailsRequest).convertToJson();
    }

}
