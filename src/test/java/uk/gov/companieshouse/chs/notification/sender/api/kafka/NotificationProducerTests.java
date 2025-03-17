package uk.gov.companieshouse.chs.notification.sender.api.kafka;

import static org.mockito.BDDMockito.given;

import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.util.concurrent.ExecutionException;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.support.SendResult;

@ExtendWith(MockitoExtension.class)
@Tag("unit-test")
public class NotificationProducerTests {

    private static final String EMAIL_TOPIC = "chs-notification-email";
    private static final String LETTER_TOPIC = "chs-notification-letter";

    private final byte[] myByte = new byte[]{0x1};

    private final SendResult<String, byte[]> result = new SendResult<>(new ProducerRecord<String,byte[]>(EMAIL_TOPIC, myByte),
        null);
    @Mock
    private KafkaProducerConfig kafkaProducerConfig;

    @Mock
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    @InjectMocks
    private NotificationProducer producer;

    @BeforeEach
    public void test() {
        MockitoAnnotations.openMocks(this);
        producer = new NotificationProducer(EMAIL_TOPIC,LETTER_TOPIC,kafkaProducerConfig,kafkaTemplate);
    }

    @Test
    public void sendEmail__ok() throws NotificationSendingException, ExecutionException, InterruptedException {
        given(kafkaTemplate.send(ArgumentMatchers.<ProducerRecord<String,byte[]>>any())).willReturn(CompletableFuture.completedFuture(result));
        Assertions.assertDoesNotThrow(() ->producer.sendEmail(myByte));
    }

    @Test
    public void sendEmail__bad() throws NotificationSendingException, ExecutionException, InterruptedException {
        given(kafkaTemplate.send(ArgumentMatchers.<ProducerRecord<String,byte[]>>any())).willThrow(new NotificationSendingException("Ow", new Throwable("ow")));
        Assertions.assertThrows(NotificationSendingException.class, () -> producer.sendEmail(myByte));
    }

    @Test
    public void sendLetter__ok() throws NotificationSendingException, ExecutionException, InterruptedException {
        given(kafkaTemplate.send(ArgumentMatchers.<ProducerRecord<String,byte[]>>any())).willReturn(CompletableFuture.completedFuture(result));
        Assertions.assertDoesNotThrow(() ->producer.sendLetter(myByte));
    }

    @Test
    public void sendLetter__bad() throws NotificationSendingException, ExecutionException, InterruptedException {
        given(kafkaTemplate.send(ArgumentMatchers.<ProducerRecord<String,byte[]>>any())).willThrow(new NotificationSendingException("Ow", new Throwable("ow")));
        Assertions.assertThrows(NotificationSendingException.class, () -> producer.sendLetter(myByte));
    }
}
