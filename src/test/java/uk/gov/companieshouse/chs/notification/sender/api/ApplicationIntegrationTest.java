package uk.gov.companieshouse.chs.notification.sender.api;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;
import uk.gov.companieshouse.api.chs.notification.sender.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.repository.NotificationEmailRequestRepository;

import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
@Testcontainers(disabledWithoutDocker = true)
@EmbeddedKafka(topics = {"chs-notification-email", "chs-notification-letter"})
@AutoConfigureTestRestTemplate
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationIntegrationTest extends AbstractMongoDBTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Container
    static S3MockContainer s3Mock = new S3MockContainer();

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("chs.notification.aws.s3-endpoint", s3Mock::getS3MockEndpoint);
        System.setProperty("aws.accessKeyId", s3Mock.getAccessKeyId());
        System.setProperty("aws.secretAccessKey", s3Mock.getSecretAccessKey());
        registry.add("chs.notification.aws.region", s3Mock::getRegion);
        registry.add("chs.notification.aws.bucket-name", s3Mock::getBucket);
        registry.add("chs.notification.aws.path-style-access-enabled", () -> true);
    }

    private Consumer<Integer, String> notificationEmailConsumer;

    @Override
    @AfterEach
    void tearDown() {
        if (notificationEmailConsumer != null) {
            notificationEmailConsumer.close();
            notificationEmailConsumer = null;
        }
    }

    @Test
    void shouldSendEmailNotification(@Autowired TestRestTemplate testRestTemplate) {
        // Given
        GovUkEmailDetailsRequest emailRequest = TestUtil.createValidEmailRequest();
        emailRequest.getSenderDetails().setReference(UUID.randomUUID().toString());

        // When
        ResponseEntity<Void> responseEntity = testRestTemplate.exchange(
                emailNotification()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(emailRequest), Void.class);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        notificationEmailConsumer = createNotificationEmailConsumer(embeddedKafkaBroker);
        ConsumerRecords<Integer, String> replies = KafkaTestUtils.getRecords(notificationEmailConsumer);
        assertThat(replies)
                .satisfiesOnlyOnce(consumerRecord ->
                        assertThat(consumerRecord.value())
                                .isEqualTo("\u0016test-app-idH" + emailRequest.getSenderDetails().getReference()));
    }

    @Test
    void shouldSendEmailNotificationWithAttachment(@Autowired TestRestTemplate testRestTemplate,
                                                   @Autowired JsonMapper objectMapper,
                                                   @Autowired S3Client s3Client,
                                                   @Autowired NotificationEmailRequestRepository notificationEmailRequestRepository) throws Exception {
        // Given
        GovUkEmailDetailsRequest emailRequest = TestUtil.createValidEmailRequest();
        emailRequest.getSenderDetails().setReference(UUID.randomUUID().toString());
        String attachmentId = emailRequest.getSenderDetails().getAppId() + "-" + emailRequest.getSenderDetails().getReference();

        MultiValueMap<String, Object> multiPartFormData = buildMultiPartFormWithAttachment(objectMapper, emailRequest);

        // When
        ResponseEntity<Void> responseEntity = whenPostEmailNotification(testRestTemplate, multiPartFormData);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assertThat(notificationEmailRequestRepository.findByUniqueReference(
                emailRequest.getSenderDetails().getAppId(),
                emailRequest.getSenderDetails().getReference()))
                .isPresent()
                .hasValueSatisfying(notificationEmailRequest -> assertThat(
                        notificationEmailRequest.getRequest().getEmailDetails().getAttachmentId()).isEqualTo(attachmentId));

        notificationEmailConsumer = createNotificationEmailConsumer(embeddedKafkaBroker);
        ConsumerRecords<Integer, String> replies = KafkaTestUtils.getRecords(notificationEmailConsumer);
        assertThat(replies)
                .satisfiesOnlyOnce(consumerRecord ->
                        assertThat(consumerRecord.value())
                                .isEqualTo("\u0016test-app-idH" + emailRequest.getSenderDetails().getReference()));

        ResponseInputStream<GetObjectResponse> object = s3Client.getObject(GetObjectRequest.builder()
                .bucket(s3Mock.getBucket())
                .key("local/" + attachmentId)
                .build());
        assertThat(new String(object.readAllBytes())).isEqualTo("Hello World");
    }

    @Test
    void shouldRespondWithConflictGivenEmailHasAlreadyBeenProcessed(@Autowired TestRestTemplate testRestTemplate,
                                                                    @Autowired JsonMapper objectMapper,
                                                                    CapturedOutput capturedOutput) {
        // Given
        GovUkEmailDetailsRequest emailRequest = TestUtil.createValidEmailRequest();
        emailRequest.getSenderDetails().setReference(UUID.randomUUID().toString());

        MultiValueMap<String, Object> multiPartFormData = buildMultiPartFormWithAttachment(objectMapper, emailRequest);
        ResponseEntity<Void> firstResponse = whenPostEmailNotification(testRestTemplate, multiPartFormData);

        // When
        ResponseEntity<Void> secondResponse = whenPostEmailNotification(testRestTemplate, multiPartFormData);

        // Then
        assertThat(firstResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(capturedOutput).contains(format("Duplicate email request found for %s%s",
                emailRequest.getSenderDetails().getAppId(),
                emailRequest.getSenderDetails().getReference()));
    }

    private MultiValueMap<String, Object> buildMultiPartFormWithAttachment(JsonMapper objectMapper,
                                                                           GovUkEmailDetailsRequest emailRequest) throws JacksonException {
        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> emailPayload = new HttpEntity<>(objectMapper.writeValueAsString(emailRequest), jsonHeaders);

        MultiValueMap<String, Object> multiPartFormData = new LinkedMultiValueMap<>();
        multiPartFormData.add("request", emailPayload);
        multiPartFormData.add("file", new ByteArrayResource("Hello World".getBytes()) {
            @Override
            public String getFilename() {
                return "file.text";
            }
        });
        return multiPartFormData;
    }

    private ResponseEntity<Void> whenPostEmailNotification(TestRestTemplate testRestTemplate, MultiValueMap<String, Object> multiPartFormData) {
        return testRestTemplate.exchange(
                emailNotification()
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .body(multiPartFormData), Void.class);
    }

    private RequestEntity.BodyBuilder emailNotification() {
        return RequestEntity.post("/notification-sender/email")
                .header("ERIC-Identity", "test")
                .header("ERIC-Identity-Type", "key")
                .header("ERIC-Authorised-Key-Roles", "*")
                .contentType(MediaType.MULTIPART_FORM_DATA);
    }

    private Consumer<Integer, String> createNotificationEmailConsumer(EmbeddedKafkaBroker embeddedKafkaBroker) {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(embeddedKafkaBroker, "chs-notification-email-consumer" + UUID.randomUUID(), false);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        ConsumerFactory<Integer, String> cf = new DefaultKafkaConsumerFactory<>(consumerProps);
        Consumer<Integer, String> consumer = cf.createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "chs-notification-email");
        return consumer;
    }
}
