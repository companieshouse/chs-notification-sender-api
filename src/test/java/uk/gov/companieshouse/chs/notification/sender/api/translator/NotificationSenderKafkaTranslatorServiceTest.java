package uk.gov.companieshouse.chs.notification.sender.api.translator;

import consumer.serialization.AvroSerializer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.companieshouse.chs.notification.translator.NotificationSenderKafkaTranslatorService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@Tag("unit-test")
/*@TestPropertySource(properties = {
        "kafka.topic.email=chs-notification-email",
        "kafka.topic.letter=chs-notification-letter"
})*/
public class NotificationSenderKafkaTranslatorServiceTest {

    @Mock
    private AvroSerializer avroSerializer;

    @InjectMocks
    private NotificationSenderKafkaTranslatorService notificationSenderKafkaTranslatorService;


    @Value( "${kafka.topic.email}" )
    private String emailKafkaTopic;

    @Value( "${kafka.topic.letter}" )
    private String letterKafkaTopic;


    @Test
    @DisplayName(" Serialize email notification message to avro ")
    void testTranslateNotificationToEmailKafkaMessage(){
        String emailMessage = "emailNotificationMessage";
        byte[] expectedAvroBytes = {1,2,3,4};

        when(avroSerializer.serialize(emailKafkaTopic,emailMessage)).thenReturn(expectedAvroBytes);
        byte[] actualBytes = notificationSenderKafkaTranslatorService.translateNotificationToEmailKafkaMessage(emailMessage);

        Assertions.assertArrayEquals(expectedAvroBytes, actualBytes);
        verify(avroSerializer,times(1)).serialize(emailKafkaTopic, emailMessage);

    }

    @Test
    @DisplayName(" Serialize letter notification message to avro ")
    void testTranslateNotificationToLetterKafkaMessage(){
        String letterMessage = "letterNotificationMessage";
        byte[] expectedAvroBytes = {1,2,3,4,5};
        when(avroSerializer.serialize(letterKafkaTopic,letterMessage)).thenReturn(expectedAvroBytes);
        byte[] actualBytes = notificationSenderKafkaTranslatorService.translateNotificationToLetterKafkaMessage(letterMessage);

        Assertions.assertArrayEquals(expectedAvroBytes, actualBytes);
        verify(avroSerializer,times(1)).serialize(letterKafkaTopic, letterMessage);

    }

}
