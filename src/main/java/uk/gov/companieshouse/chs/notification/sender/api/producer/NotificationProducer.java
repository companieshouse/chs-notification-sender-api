package uk.gov.companieshouse.chs.notification.sender.api.producer;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
//import uk.gov.companieshouse.kafka.message.Message;
//import uk.gov.companieshouse.kafka.producer.CHKafkaProducer;
import java.util.Date;
import uk.gov.companieshouse.chs.notification.sender.api.config.KafkaProducerConfig;
//import java.util.concurrent.ExecutionException;

@Service
public class NotificationProducer {

    @Autowired
    private KafkaProducerConfig kafkaProducerConfig;
    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate;





    @Value("${kafka.topic.email}")
    private String emailTopic;
    @Value("${kafka.topic.letter}")
    private String letterTopic;


   public void sendMessage(String topicName, byte[] message) {
       CompletableFuture<SendResult<String, byte[]>> future = kafkaTemplate.send(topicName, message);
       future.whenComplete((result, ex) -> {
           if (ex == null) {
               System.out.println("Sent message=[" + Arrays.toString(message) +
                   "] with offset=[" + result.getRecordMetadata().offset() + "]");
           } else {
               System.out.println("Unable to send message=[" +
                   Arrays.toString(message) + "] due to : " + ex.getMessage());
           }
       });
   }


    /**
     * Sends an email-send message to the Kafka producer.
     *
     */
    public void sendEmail(byte[] emailData) throws NotificationSendingException {

        final Message message = new Message();
        message.setValue(emailData);
        message.setTopic(emailTopic);
        message.setTimestamp(new Date().getTime());
        ProducerRecord<String, byte[]> record = getProducerRecordFromMessage(message);

        sendMessage(emailTopic, emailData);
        //kafkaTemplate.send(emailTopic, record.value());

    }
    /**
     * Sends an email-send message to the Kafka producer.
     *
     */
    public void sendLetter(byte[] letterData) throws NotificationSendingException {

        final Message message = new Message();
        message.setValue(letterData);
        message.setTopic(letterTopic);
        message.setTimestamp(new Date().getTime());

        ProducerRecord<String, byte[]> record = getProducerRecordFromMessage(message);

        // kafkaTemplate.send(letterTopic, record.value());
        sendMessage(letterTopic, letterData);
    }

    private ProducerRecord<String, byte[]> getProducerRecordFromMessage(Message msg) {

        return new ProducerRecord<>(
            msg.getTopic(),
            msg.getPartition(),
            msg.getTimestamp(),
            msg.getKey(),
            msg.getValue()
        );
    }
}