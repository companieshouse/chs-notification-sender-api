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
import uk.gov.companieshouse.chs.notification.sender.api.utils.StaticPropertyUtil;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
//import java.util.concurrent.ExecutionException;

@Service
public class NotificationProducer {

    private static final Logger LOG = LoggerFactory
        .getLogger(StaticPropertyUtil.APPLICATION_NAMESPACE);
    @Autowired
    private KafkaProducerConfig kafkaProducerConfig;
    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate;
    @Value("${kafka.topic.email}")
    private String emailTopic;
    @Value("${kafka.topic.letter}")
    private String letterTopic;

   private void sendMessage(String topicName, byte[] message) {
       CompletableFuture<SendResult<String, byte[]>> future =
           kafkaTemplate.send(topicName, message);
       future.whenComplete((result, ex) -> {
           if (ex == null) {
               LOG.infoContext(topicName,"Sent message=[" + Arrays.toString(message)
                   + "] with offset=[" + result.getRecordMetadata().offset() + "]" , null);
           } else {
               LOG.errorContext(topicName, new Exception("Unable to send ["
                   + Arrays.toString(message) + "] due to " + ex.getMessage()), null);
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