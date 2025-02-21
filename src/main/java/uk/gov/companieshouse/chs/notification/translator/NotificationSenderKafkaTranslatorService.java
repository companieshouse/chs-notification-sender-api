package uk.gov.companieshouse.chs.notification.translator;

import consumer.serialization.AvroSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.chs.notification.utils.StaticPropertyUtil;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class NotificationSenderKafkaTranslatorService implements NotificationSenderKafkaTranslatorInterface{

    private final String emailKafkaTopic;

    private final String letterKafkaTopic;

    private final AvroSerializer avroSerializer;

    public NotificationSenderKafkaTranslatorService(final @Value("${kafka.topic.email}") String emailTopic, final @Value("${kafka.topic.letter}") String letterTopic, final AvroSerializer avroSerializer) {
        this.emailKafkaTopic = emailTopic;
        this.letterKafkaTopic = letterTopic;
        this.avroSerializer = avroSerializer;
    }

    private static final Logger LOG = LoggerFactory.getLogger( StaticPropertyUtil.APPLICATION_NAMESPACE );

    @Override
    public byte[] translateNotificationToEmailKafkaMessage(String emailNotificationMessage) {
        LOG.debug(String.format(" serializing emailNotificationMessage - %s", emailNotificationMessage));
        return avroSerializer.serialize(emailKafkaTopic, emailNotificationMessage);
    }

    @Override
    public byte[] translateNotificationToLetterKafkaMessage(String letterNotificationMessage) {
        LOG.debug(String.format(" serializing letterNotificationMessage - %s", letterNotificationMessage));
        return avroSerializer.serialize(letterKafkaTopic, letterNotificationMessage);
    }

}
