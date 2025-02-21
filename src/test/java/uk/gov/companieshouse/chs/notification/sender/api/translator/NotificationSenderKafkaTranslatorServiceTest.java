package uk.gov.companieshouse.chs.notification.sender.api.translator;

import consumer.serialization.AvroSerializer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.chs.notification.translator.NotificationSenderKafkaTranslatorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@Tag("unit-test")
@TestPropertySource(properties = {
        "kafka.topic.email=chs-notification-email",
        "kafka.topic.letter=chs-notification-letter"
})
public class NotificationSenderKafkaTranslatorServiceTest {

    private static final String EMAIL_TOPIC = "chs-notification-email";
    private static final String LETTER_TOPIC = "chs-notification-letter";
    private static final String EMAIL_MESSAGE = "Test Email Message";
    private static final String LETTER_MESSAGE = "Test Letter Message";
    private static final byte[] SERIALIZED_EMAIL_MESSAGE = new byte[]{1, 2, 3};
    private static final byte[] SERIALIZED_LETTER_MESSAGE = new byte[]{4, 5, 6};

    @Mock
    private AvroSerializer avroSerializer;

    private NotificationSenderKafkaTranslatorService notificationSenderKafkaTranslatorService;

    @BeforeEach
    void setUp() {
        notificationSenderKafkaTranslatorService = new NotificationSenderKafkaTranslatorService(EMAIL_TOPIC, LETTER_TOPIC, avroSerializer);

        lenient().when(avroSerializer.serialize(eq(EMAIL_TOPIC), eq(EMAIL_MESSAGE))).thenReturn(SERIALIZED_EMAIL_MESSAGE);
        lenient().when(avroSerializer.serialize(eq(LETTER_TOPIC), eq(LETTER_MESSAGE))).thenReturn(SERIALIZED_LETTER_MESSAGE);

    }

    @Test
    @DisplayName(" Serialize email notification message to avro ")
    void testTranslateNotificationToEmailKafkaMessage(){
        byte[] result = notificationSenderKafkaTranslatorService.translateNotificationToEmailKafkaMessage(EMAIL_MESSAGE);
        assertArrayEquals(SERIALIZED_EMAIL_MESSAGE, result);
    }

    @Test
    @DisplayName(" Serialize letter notification message to avro ")
    void testTranslateNotificationToLetterKafkaMessage(){
        byte[] result = notificationSenderKafkaTranslatorService.translateNotificationToLetterKafkaMessage(LETTER_MESSAGE);
        assertArrayEquals(SERIALIZED_LETTER_MESSAGE, result);
        verify(avroSerializer).serialize(eq(LETTER_TOPIC), eq(LETTER_MESSAGE));
    }

    @Test
    @DisplayName(" Empty message should return empty byte array or throw an exception ")
    void testEmptyMessageReturnsEmptyArrayOrExceptionLetterKafkaMessage(){
        when(avroSerializer.serialize(eq(EMAIL_TOPIC), anyString())).thenThrow(new RuntimeException("Serialization failed"));
        Exception exception = assertThrows(RuntimeException.class, () -> notificationSenderKafkaTranslatorService.translateNotificationToEmailKafkaMessage(EMAIL_MESSAGE));
        assertEquals("Serialization failed", exception.getMessage());
    }

}
