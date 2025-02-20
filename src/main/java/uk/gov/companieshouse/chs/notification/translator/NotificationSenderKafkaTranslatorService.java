package uk.gov.companieshouse.chs.notification.translator;

import consumer.serialization.AvroSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.chs.notification.utils.StaticPropertyUtil;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class NotificationSenderKafkaTranslatorService implements NotificationSenderKafkaTranslatorInterface{

    @Value( "${kafka.topic.email}" )
    private String emailKafkaTopic;

    @Value( "${kafka.topic.letter}" )
    private String letterKafkaTopic;

    private final AvroSerializer avroSerializer;

    private static final Logger LOG = LoggerFactory.getLogger( StaticPropertyUtil.APPLICATION_NAMESPACE );

    NotificationSenderKafkaTranslatorService() {
        this.avroSerializer = new AvroSerializer();
    }

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
