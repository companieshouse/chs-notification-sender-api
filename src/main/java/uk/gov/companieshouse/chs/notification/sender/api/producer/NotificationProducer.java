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
    // serialisedMessage

    public NotificationProducer(
        final CHKafkaProducer chKafkaProducer) {
        this.chKafkaProducer = chKafkaProducer;
    }

    /**
     * Sends an email-send message to the Kafka producer.
     *
     * @throws  EmailSendingException should a failure to build the email occur
     */
    public void sendEmail(byte[] emailData) throws EmailSendingException {
        try {
            final Message message = new Message();
            message.setValue(emailData);
            message.setTopic(emailTopic);
            message.setTimestamp(new Date().getTime());

            chKafkaProducer.send(message);


        } catch (ExecutionException e) {
            throw new EmailSendingException("Error sending message to kafka", e);
        } catch (InterruptedException e) {
            throw new EmailSendingException("Error - thread interrupted", e);
        }
    }
//    public letterProducer(final EmailFactory emailFactory, final AvroSerializer<Email> serializer,
//                         final CHKafkaProducer chKafkaProducer, @Value("${email.producer.appId}") final String appId) {
//        this.emailFactory = emailFactory;
//        this.serializer = serializer;
//        this.chKafkaProducer = chKafkaProducer;
//        this.appId = appId;
//    }

}