package uk.gov.companieshouse.chs.notification.sender.api.producer;

import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NotificationProducerTests {

    private static final String EMAIL_TOPIC = "${kafka.topic.email}";
    private static final String LETTER_TOPIC = "${kafka.topic.letter}";;

    @Test
    void sendEmail__ok() throws NotificationSendingException {
        byte[] myByte = new byte[]{0x1};
        MockProducer<String, byte[]> mockProducer = new MockProducer<>(true, new StringSerializer(), new ByteArraySerializer());

        NotificationProducer notificationProducer = new NotificationProducer(mockProducer);
        notificationProducer.sendEmail(myByte, EMAIL_TOPIC);
        Assertions.assertEquals(1, mockProducer.history().size());
        Assertions.assertTrue(mockProducer.history().getFirst().topic().equalsIgnoreCase("${kafka.topic.email}"));

    }

    @Test
    void sendLetter__ok() throws NotificationSendingException {
        byte[] myByte = new byte[]{0x1};
        MockProducer<String, byte[]> mockProducer = new MockProducer<>(true, new StringSerializer(), new ByteArraySerializer());

        NotificationProducer notificationProducer = new NotificationProducer(mockProducer);
        notificationProducer.sendEmail(myByte, LETTER_TOPIC);
        Assertions.assertEquals(1, mockProducer.history().size());
        Assertions.assertTrue(mockProducer.history().getFirst().topic().equalsIgnoreCase("${kafka.topic.letter}"));

    }
}
