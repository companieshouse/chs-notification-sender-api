package uk.gov.companieshouse.chs.notification.sender.api.producer;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
//import uk.gov.companieshouse.kafka.producer.CHKafkaProducer;
import java.util.concurrent.ExecutionException;

@Disabled
public class NotificationProducerTests {

    private NotificationProducer notificationProducer;
   // @Mock private CHKafkaProducer chKafkaProducer;

    @Test
    void sendEmail__ok() throws NotificationSendingException, ExecutionException, InterruptedException {

    }
}
