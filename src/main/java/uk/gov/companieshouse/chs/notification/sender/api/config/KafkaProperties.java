package uk.gov.companieshouse.chs.notification.sender.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaProperties {
    private final String brokerAddress;
    private final String acknowledgementsName;
    private final int retries;
    private final int maxBlockMilliseconds;
    private final String emailTopic;
    private final String letterTopic;

    public KafkaProperties(
            @Value("${spring.kafka.bootstrap-servers}") final String brokerAddress,
            @Value("${kafka.config.acks}") final String acknowledgementsName,
            @Value("${kafka.max-attempts}") final int retries,
            @Value("${kafka.max-block-milliseconds}") final int maxBlockMilliseconds,
            @Value("${kafka.topic.email}") final String emailTopic,
            @Value("${kafka.topic.letter}") final String letterTopic
    ) {
        this.brokerAddress = brokerAddress;
        this.acknowledgementsName = acknowledgementsName;
        this.retries = retries;
        this.maxBlockMilliseconds = maxBlockMilliseconds;
        this.emailTopic = emailTopic;
        this.letterTopic = letterTopic;
    }

    public String getBrokerAddress() {
        return brokerAddress;
    }

    public String getAcknowledgementsName() {
        return acknowledgementsName;
    }

    public int getRetries() {
        return retries;
    }

    public int getMaxBlockMilliseconds() {
        return maxBlockMilliseconds;
    }

    public String getEmailTopic() {
        return emailTopic;
    }

    public String getLetterTopic() {
        return letterTopic;
    }
}
