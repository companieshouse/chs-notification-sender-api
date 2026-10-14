package uk.gov.companieshouse.chs.notification.sender.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * This is the entry point for this service
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class ChsNotificationSenderApiApplication {

    public static final String APPLICATION_NAMESPACE = "chs-notification-sender-api";

    public static void main(String[] args) {
        SpringApplication.run(ChsNotificationSenderApiApplication.class, args);
    }

}
