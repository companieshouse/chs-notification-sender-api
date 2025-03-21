package uk.gov.companieshouse.chs.notification.sender.api.translator;

import consumer.serialization.AvroSerializer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkLetterDetailsRequest;
import uk.gov.companieshouse.notification.ChsEmailNotification;
import uk.gov.companieshouse.notification.ChsLetterNotification;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit-test")
public class KafkaTranslatorServiceTest {

    private static final String EMAIL_TOPIC = "chs-notification-email";
    private static final String LETTER_TOPIC = "chs-notification-letter";

   @Mock
    private AvroSerializer avroSerializer;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private KafkaTranslatorService kafkaTranslatorService;

    @BeforeEach
    void setUp() {
        kafkaTranslatorService = new KafkaTranslatorService(EMAIL_TOPIC, LETTER_TOPIC, avroSerializer, notificationMapper);
    }

    @Test
    void testTranslateNotificationToEmailKafkaMessage() {
        GovUkEmailDetailsRequest emailRequest = new GovUkEmailDetailsRequest();
        ChsEmailNotification mappedRequest = mock(ChsEmailNotification.class);

        byte[] expectedBytes = new byte[]{1, 2, 3};

        when(notificationMapper.mapToEmailDetailsRequest(emailRequest)).thenReturn(mappedRequest);
        when(avroSerializer.serialize(EMAIL_TOPIC, mappedRequest)).thenReturn(expectedBytes);

        byte[] result = kafkaTranslatorService.translateNotificationToEmailKafkaMessage(emailRequest);

        assertArrayEquals(expectedBytes, result);
        verify(notificationMapper, times(1)).mapToEmailDetailsRequest(emailRequest);
        verify(avroSerializer, times(1)).serialize(EMAIL_TOPIC, mappedRequest);
    }

    @Test
    void testTranslateNotificationToLetterKafkaMessage() {
        GovUkLetterDetailsRequest letterRequest = new GovUkLetterDetailsRequest();
        ChsLetterNotification mappedRequest = mock(ChsLetterNotification.class);

        byte[] expectedBytes = new byte[]{4, 5, 6};

        when(notificationMapper.mapToLetterDetailsRequest(letterRequest)).thenReturn(mappedRequest);

        when(avroSerializer.serialize(LETTER_TOPIC, mappedRequest)).thenReturn(expectedBytes);

        byte[] result = kafkaTranslatorService.translateNotificationToLetterKafkaMessage(letterRequest);

        assertArrayEquals(expectedBytes, result);
        verify(notificationMapper, times(1)).mapToLetterDetailsRequest(letterRequest);
        verify(avroSerializer, times(1)).serialize(LETTER_TOPIC, mappedRequest);
    }

    @Test
    void testTranslateNotificationToEmailKafkaMessageThrowsException() {
        GovUkEmailDetailsRequest emailRequest = new GovUkEmailDetailsRequest();

        doThrow(new IllegalArgumentException("Invalid message format"))
                .when(notificationMapper).mapToEmailDetailsRequest(emailRequest);

        assertThrows(IllegalArgumentException.class, () -> kafkaTranslatorService.translateNotificationToEmailKafkaMessage(emailRequest));
        verify(notificationMapper, times(1)).mapToEmailDetailsRequest(emailRequest);
    }

    @Test
    void testTranslateNotificationToLetterKafkaMessageThrowsException() {
        GovUkLetterDetailsRequest letterRequest = new GovUkLetterDetailsRequest();

        doThrow(new IllegalArgumentException("Invalid message format"))
                .when(notificationMapper).mapToLetterDetailsRequest(letterRequest);

        assertThrows(IllegalArgumentException.class, () -> kafkaTranslatorService.translateNotificationToLetterKafkaMessage(letterRequest));
        verify(notificationMapper, times(1)).mapToLetterDetailsRequest(letterRequest);
    }
}
