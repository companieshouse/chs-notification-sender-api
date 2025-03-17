package uk.gov.companieshouse.chs.notification.sender.api.kafka;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.chs.notification.sender.api.utils.StaticPropertyUtil;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
class NotificationProducer implements KafkaProducerInterface{

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

   private void sendMessage(String topicName, byte[] message)   throws NotificationSendingException {
       ProducerRecord<String, byte[]> producerRecord= new ProducerRecord<>(topicName, message);
       CompletableFuture<SendResult<String, byte[]>> future =
           kafkaTemplate.send(producerRecord);
       future.whenComplete((result, ex) -> {
           if (ex == null) {
               LOG.infoContext(topicName,"Sent message=[" + Arrays.toString(message)
                   + "] with offset=[" + result.getRecordMetadata().offset() + "]" , null);
           } else {
               LOG.errorContext(topicName, new Exception("Unable to send ["
                   + Arrays.toString(message) + "] due to " + ex.getMessage()), null);
               throw new NotificationSendingException("Unable to send " + topicName + " " + ex.getMessage(),ex);
           }
       });
   }

    public NotificationProducer(@Value("${kafka.topic.email}") String emailTopic, @Value("${kafka.topic.letter}") String letterTopic,
    KafkaProducerConfig config, KafkaTemplate<String, byte[]> template ){
       this.emailTopic = emailTopic;
       this.letterTopic = letterTopic;
       this.kafkaProducerConfig = config;
       this.kafkaTemplate = template;
    }
    /**
     * Sends an email-send message to the Kafka producer.
     *
     */
    public void sendEmail(byte[] emailData) throws NotificationSendingException {
        sendMessage(emailTopic, emailData);
    }

    /**
     * Sends an email-send message to the Kafka producer.
     *
     */
    public void sendLetter(byte[] letterData) throws NotificationSendingException {
        sendMessage(letterTopic, letterData);
    }

}