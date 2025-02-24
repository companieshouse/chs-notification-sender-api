package uk.gov.companieshouse.chs.notification.sender.api.translator;

import consumer.serialization.AvroSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.chs.notification.sender.api.utils.StaticPropertyUtil;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
class KafkaTranslatorService implements KafkaTranslatorInterface {

    private final String emailKafkaTopic;

    private final String letterKafkaTopic;

    private final AvroSerializer avroSerializer;

    public KafkaTranslatorService(@Value("${kafka.topic.email}") String emailTopic, @Value("${kafka.topic.letter}") String letterTopic, AvroSerializer avroSerializer) {
        this.emailKafkaTopic = emailTopic;
        this.letterKafkaTopic = letterTopic;
        this.avroSerializer = avroSerializer;
    }

    private static final Logger LOG = LoggerFactory.getLogger(StaticPropertyUtil.APPLICATION_NAMESPACE);

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
