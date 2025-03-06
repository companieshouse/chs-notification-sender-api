package uk.gov.companieshouse.chs.notification.sender.api.producer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProducerConfig {
    private String keySerializer = "org.apache.kafka.common.serialization.StringSerializer";
    private String valueSerializer = "org.apache.kafka.common.serialization.ByteArraySerializer";
    private String[] brokerAddresses;
    private Acks acks;
    private int retries;
    private boolean roundRobinPartitioner;
    private int maxBlockMilliseconds = 1000;
    private int requestTimeoutMilliseconds = 1000;
    private boolean enableIdempotence = true;

    public String[] getBrokerAddresses() {
        return brokerAddresses;
    }

    public void setBrokerAddresses(String[] brokerAddresses) {
        this.brokerAddresses = brokerAddresses;
    }

    public Acks getAcks() {
        return acks;
    }

    public void setAcks(Acks acks) {
        this.acks = acks;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public boolean isRoundRobinPartitioner() {
        return roundRobinPartitioner;
    }

    public void setRoundRobinPartitioner(boolean roundRobinPartitioner) {
        this.roundRobinPartitioner = roundRobinPartitioner;
    }

    public String getKeySerializer() {
        return keySerializer;
    }

    public void setKeySerializer(String keySerializer) {
        this.keySerializer = keySerializer;
    }

    public String getValueSerializer() {
        return valueSerializer;
    }

    public void setValueSerializer(String valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    public int getMaxBlockMilliseconds() {
        return maxBlockMilliseconds;
    }

    public void setMaxBlockMilliseconds(int maxBlockMilliseconds) {
        this.maxBlockMilliseconds = maxBlockMilliseconds;
    }

    public int getRequestTimeoutMilliseconds() {
        return requestTimeoutMilliseconds;
    }

    public void setRequestTimeoutMilliseconds(int requestTimeoutMilliseconds) {
        this.requestTimeoutMilliseconds = requestTimeoutMilliseconds;
    }

    public boolean isEnableIdempotence() {
        return enableIdempotence;
    }

    public void setEnableIdempotence(boolean enableIdempotence) {
        this.enableIdempotence = enableIdempotence;
    }
}
