package uk.gov.companieshouse.chs.notification.sender.api.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.kafka.producer.CHKafkaProducer;
import java.util.Date;
import java.util.concurrent.ExecutionException;

@Service
public class NotificationProducer {

    private final CHKafkaProducer chKafkaProducer;

    @Value("${kafka.topic.email}")
    private String emailTopic;
    @Value("${kafka.topic.letter}")
    private String letterTopic;

    public NotificationProducer(
        final CHKafkaProducer chKafkaProducer) {
        this.chKafkaProducer = chKafkaProducer;
    }

    /**
     * Sends an email-send message to the Kafka producer.
     *
     */
    public void sendEmail(byte[] emailData) throws NotificationSendingException {
        try {
            final Message message = new Message();
            message.setValue(emailData);
            message.setTopic(emailTopic);
            message.setTimestamp(new Date().getTime());

            chKafkaProducer.send(message);


        } catch (ExecutionException e) {
            throw new NotificationSendingException("Error sending message to kafka", e);
        } catch (InterruptedException e) {
            throw new NotificationSendingException("Error - thread interrupted", e);
        }
    }
    /**
     * Sends an email-send message to the Kafka producer.
     *
     */
    public void sendLetter(byte[] letterData) throws NotificationSendingException {
        try {
            final Message message = new Message();
            message.setValue(letterData);
            message.setTopic(letterTopic);
            message.setTimestamp(new Date().getTime());

            chKafkaProducer.send(message);

        } catch (ExecutionException e) {
            throw new NotificationSendingException("Error sending message to kafka", e);
        } catch (InterruptedException e) {
            throw new NotificationSendingException("Error - thread interrupted", e);
        }
    }
}