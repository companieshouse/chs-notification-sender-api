package uk.gov.companieshouse.chs.notification.sender.api.producer;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.chs.notification.sender.api.utils.StaticPropertyUtil;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class NotificationProducer {

    private static final Logger LOG = LoggerFactory.getLogger(StaticPropertyUtil.APPLICATION_NAMESPACE);

    @Value("${kafka.topic.email}")
    private String emailTopic;
    @Value("${kafka.topic.letter}")
    private String letterTopic;


    private final Producer<String, byte[]> producer;

    public NotificationProducer(Producer<String, byte[]> producer) {
        this.producer = producer;
    }

     /**
     * Sends an email-send message to the Kafka producer.
     */
    public void sendEmail(byte[] emailData, String emailTopic) throws NotificationSendingException {

        ProducerRecord<String, byte[]> emailRecord = new ProducerRecord<>(emailTopic, emailData);
        LOG.info("Sending to message to " + emailTopic);

        // send data
        producer.send(emailRecord);

        // tell producer to send all data and block until complete
        producer.flush();

        // close the producer
        producer.close();

    }

     /**
     * Sends a letter-send message to the Kafka producer.
     */
    public void sendLetter(byte[] letterData, String letterTopic) throws NotificationSendingException {

        ProducerRecord<String, byte[]> letterRecord = new ProducerRecord<>(letterTopic, letterData);
        LOG.info("Sending to message to " + letterTopic);

        // send data
        producer.send(letterRecord);

        // tell producer to send all data and block until complete
        producer.flush();

        // close the producer
        producer.close();
    }
}