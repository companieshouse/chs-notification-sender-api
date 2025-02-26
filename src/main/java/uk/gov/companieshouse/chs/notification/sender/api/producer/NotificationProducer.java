package uk.gov.companieshouse.chs.notification.sender.api.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import consumer.serialization.AvroSerializer;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.companieshouse.api.chs_notification_sender.api.NotificationSenderInterface;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.kafka.producer.CHKafkaProducer;

import java.util.Date;
import java.util.concurrent.ExecutionException;

public class NotificationProducer {


    private static final String EMAIL_SEND_TOPIC = "email-send";
    private final AvroSerializer serializer;
    private final KafkaProducerFactory kafkaProducerFactory;
    private final CHKafkaProducer chKafkaProducer;
    private final NotificationSenderInterface notificationSenderInterface;
    private byte EmailTopic emailTopic;

    // serialisedMessage

    public NotificationProducer(final KafkaProducerFactory kafkaProducerFactory, final AvroSerializer serializer,
                                final CHKafkaProducer chKafkaProducer, final NotificationSenderInterface notificationSenderInterface, @Value("${kafka.topic.email}") final byte emailTopic) {
        this.kafkaProducerFactory = kafkaProducerFactory;
        this.serializer = serializer;
        this.chKafkaProducer = chKafkaProducer;
        this.notificationSenderInterface = notificationSenderInterface;
        this.emailTopic = emailTopic;
    }

    /**
     * Sends an email-send message to the Kafka producer.
     *
     * @throws JsonProcessingException should a failure to build the email occur
     * @throws SerializationException  should there be a failure to serialize the EmailSend object
     * @throws ExecutionException      should something unexpected happen
     * @throws InterruptedException    should something unexpected happen
     */
    public void sendEmail(byte emailData) throws EmailSendingException {
        try {
            final Message message = new Message();
            message.setValue(serializer.serialize(EMAIL_SEND_TOPIC, emailData));
            message.setTopic(EMAIL_SEND_TOPIC);
            message.setTimestamp(new Date().getTime());

            chKafkaProducer.send(message);


        } catch (ExecutionException e) {
            throw new EmailSendingException("Error sending message to kafka", e);
        } catch (InterruptedException e) {
            throw new EmailSendingException("Error - thread interrupted", e);
        }

//    public letterProducer(final EmailFactory emailFactory, final AvroSerializer<Email> serializer,
//                         final CHKafkaProducer chKafkaProducer, @Value("${email.producer.appId}") final String appId) {
//        this.emailFactory = emailFactory;
//        this.serializer = serializer;
//        this.chKafkaProducer = chKafkaProducer;
//        this.appId = appId;
//    }
    }
}