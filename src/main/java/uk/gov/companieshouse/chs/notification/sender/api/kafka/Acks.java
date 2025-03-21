package uk.gov.companieshouse.chs.notification.sender.api.kafka;

enum Acks {
    NO_RESPONSE("0"),
    WAIT_FOR_LOCAL("1"),
    WAIT_FOR_ALL("-1");

    private String code;

    private Acks(String code) {
        this.code = code;
    }

    /**
     * Get ack code to match Apache Kafka's ack conventions See
     * <a href="https://kafka.apache.org/documentation">...</a> Producer Configs
     * for official documentation.
     *
     * @return code
     */
    public String getCode() {
        return code;
    }
}