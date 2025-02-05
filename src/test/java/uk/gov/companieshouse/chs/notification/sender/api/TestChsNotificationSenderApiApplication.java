package uk.gov.companieshouse.chs.notification.sender.api;

import org.springframework.boot.SpringApplication;

public class TestChsNotificationSenderApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(ChsNotificationSenderApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
