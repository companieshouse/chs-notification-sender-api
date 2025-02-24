package uk.gov.companieshouse.chs.notification.sender.api.translator;

import consumer.serialization.AvroSerializer;
import org.apache.avro.AvroRuntimeException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.chs.notification.sender.api.translator.KafkaTranslatorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit-test")
public class KafkaTranslatorServiceTest {

    private static final String EMAIL_TOPIC = "chs-notification-email";
    private static final String LETTER_TOPIC = "chs-notification-letter";
    private static final String EMAIL_MESSAGE = "Test Email Message";
    private static final String LETTER_MESSAGE = "Test Letter Message";
    private static final byte[] SERIALIZED_EMAIL_MESSAGE = new byte[]{1, 2, 3};
    private static final byte[] SERIALIZED_LETTER_MESSAGE = new byte[]{4, 5, 6};

    @Mock
    private AvroSerializer avroSerializer;

    @InjectMocks
    private KafkaTranslatorService kafkaTranslatorService;

    @BeforeEach
    void setUp() {
        kafkaTranslatorService = new KafkaTranslatorService(EMAIL_TOPIC, LETTER_TOPIC, avroSerializer);
    }

    @Test
    @DisplayName(" Serialize email notification message to avro ")
    void testTranslateNotificationToEmailKafkaMessage() {
        when(avroSerializer.serialize(EMAIL_TOPIC, EMAIL_MESSAGE)).thenReturn(SERIALIZED_EMAIL_MESSAGE);

        byte[] result = kafkaTranslatorService.translateNotificationToEmailKafkaMessage(EMAIL_MESSAGE);

        assertArrayEquals(SERIALIZED_EMAIL_MESSAGE, result);
        verify(avroSerializer, times(1)).serialize(EMAIL_TOPIC, EMAIL_MESSAGE);
    }

    @Test
    @DisplayName(" Serialize letter notification message to avro ")
    void testTranslateNotificationToLetterKafkaMessage() {
        when(avroSerializer.serialize(LETTER_TOPIC, LETTER_MESSAGE)).thenReturn(SERIALIZED_LETTER_MESSAGE);

        byte[] result = kafkaTranslatorService.translateNotificationToLetterKafkaMessage(LETTER_MESSAGE);

        assertArrayEquals(SERIALIZED_LETTER_MESSAGE, result);
        verify(avroSerializer, times(1)).serialize(LETTER_TOPIC, LETTER_MESSAGE);
    }

    @Test
    @DisplayName(" Empty message should throw an exception ")
    void testEmptyMessageReturnsEmptyArrayOrExceptionLetterKafkaMessage() {
        when(avroSerializer.serialize(eq(EMAIL_TOPIC), anyString())).thenThrow(new RuntimeException("Serialization failed"));
        Exception exception = assertThrows(RuntimeException.class, () -> kafkaTranslatorService.translateNotificationToEmailKafkaMessage(EMAIL_MESSAGE));
        assertEquals("Serialization failed", exception.getMessage());
    }

    @Test
    @DisplayName(" Invalid schema should throw AvroRuntimeException ")
    void testSerializeInvalidSchemaThrowsAvroRuntimeException() {
        String invalidMessage = "{\"invalidField\":\"value\"}";
        when(avroSerializer.serialize(eq(EMAIL_TOPIC), eq(invalidMessage))).thenThrow(new AvroRuntimeException("Serialization failed"));

        Exception exception = assertThrows(AvroRuntimeException.class, () -> kafkaTranslatorService.translateNotificationToEmailKafkaMessage(invalidMessage));
        assertEquals("Serialization failed", exception.getMessage());
    }

}
