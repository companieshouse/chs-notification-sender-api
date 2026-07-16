package uk.gov.companieshouse.chs.notification.sender.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import uk.gov.companieshouse.api.chs.notification.sender.model.GovUkEmailDetailsRequest;

@EmbeddedKafka(partitions = 1, topics = {"chs-notification-email", "chs-notification-letter"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationIntegrationTest extends AbstractMongoDBTest {

    @Test
    void shouldSendEmailNotification(@Autowired TestRestTemplate testRestTemplate,
                                     @Autowired EmbeddedKafkaBroker embeddedKafkaBroker) {
        // Given
        GovUkEmailDetailsRequest emailRequest = TestUtil.createValidEmailRequest();

        // When
        ResponseEntity<Void> responseEntity = testRestTemplate.exchange(RequestEntity.post("/notification-sender/email")
                .contentType(MediaType.APPLICATION_JSON)
                .header("ERIC-Identity", "test")
                .header("ERIC-Identity-Type", "key")
                .header("ERIC-Authorised-Key-Roles", "*")
                .body(emailRequest), Void.class);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Consumer<Integer, String> consumer = createNotificationEmailConsumer(embeddedKafkaBroker);
        ConsumerRecords<Integer, String> replies = KafkaTestUtils.getRecords(consumer);
        assertThat(replies)
                .singleElement()
                .satisfies(consumerRecord ->
                        assertThat(consumerRecord.value())
                                .isEqualTo("\u0016test-app-id\u001Ctest-reference"));
    }

    private Consumer<Integer, String> createNotificationEmailConsumer(EmbeddedKafkaBroker embeddedKafkaBroker) {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("chs-notification-email-consumer", "true", embeddedKafkaBroker);
        ConsumerFactory<Integer, String> cf = new DefaultKafkaConsumerFactory<>(consumerProps);
        Consumer<Integer, String> consumer = cf.createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "chs-notification-email");
        return consumer;
    }
}
