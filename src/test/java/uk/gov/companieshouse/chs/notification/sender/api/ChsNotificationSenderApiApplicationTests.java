package uk.gov.companieshouse.chs.notification.sender.api;


import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChsNotificationSenderApiApplicationTests {

   @Value("${test.property.value}")
     private String testPropertyValue;

    @Test
    void testDefaultPropertiesFile() {
        assertThat(testPropertyValue)
            .as("Default property value should match")
            .isEqualTo("default-value");
    }

    @Test
    void contextLoads() {
    }

}
