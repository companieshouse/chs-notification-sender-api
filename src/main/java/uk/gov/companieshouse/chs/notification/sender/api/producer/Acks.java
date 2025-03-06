package uk.gov.companieshouse.chs.notification.sender.api.producer;

public enum Acks {
    NO_RESPONSE("0"),
    WAIT_FOR_LOCAL("1"),
    WAIT_FOR_ALL("-1");

    private String code;

    private Acks(String code) {
        this.code = code;
    }

    /**
     * Get ack codae to match Apache Kafka's ack conventions See
     * https://kafka.apache.org/documentation Producer Configs
     * for official documentation.
     *
     * @return code
     */
    public String getCode() {
        return code;
    }
}
