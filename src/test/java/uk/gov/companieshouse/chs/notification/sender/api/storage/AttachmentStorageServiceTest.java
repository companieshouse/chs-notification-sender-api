package uk.gov.companieshouse.chs.notification.sender.api.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import uk.gov.companieshouse.api.chs.notification.sender.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.chs.notification.sender.api.S3MockContainer;
import uk.gov.companieshouse.chs.notification.sender.api.TestUtil;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment =  SpringBootTest.WebEnvironment.RANDOM_PORT)
class AttachmentStorageServiceTest {

    public static final String NOTIFICATION_ATTACHMENTS = "notification-attachments";

    @Autowired
    private AttachmentStorageService attachmentStorageService;

    @Autowired
    private S3Client s3Client;

    @Container
    static S3MockContainer s3Mock = new S3MockContainer();

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("chs.notification.aws.s3-endpoint", s3Mock::getS3MockEndpoint);
        registry.add("chs.notification.aws.access-key-id", s3Mock::getAccessKeyId);
        registry.add("chs.notification.aws.secret-access-key", s3Mock::getSecretAccessKey);
        registry.add("chs.notification.aws.region", s3Mock::getRegion);
        registry.add("chs.notification.aws.bucket-name", s3Mock::getBucket);
        registry.add("chs.notification.aws.path-style-access-enabled", () -> true);
    }

    @ParameterizedTest
    @ValueSource(strings = { "attachment.txt", "image.pdf" })
    void shouldStoreDifferentContentTypeAttachments(String fileName) throws IOException {
        // Given
        GovUkEmailDetailsRequest emailRequest = TestUtil.createValidEmailRequest();
        emailRequest.getSenderDetails().setReference(UUID.randomUUID().toString());

        ClassPathResource attachmentResource = getAttachmentResource(fileName);
        MultipartFile multipartFile = givenMultipartFileForAttachment(attachmentResource);

        // When
        attachmentStorageService.storeAttachment(emailRequest, multipartFile);

        // Then
        HeadObjectResponse headObjectResponse = getHeadeObjectForRequest(emailRequest);
        ResponseInputStream<GetObjectResponse> storedObject = s3Client.getObject(GetObjectRequest.builder()
                .bucket(s3Mock.getBucket())
                .key(emailRequest.getSenderDetails().getReference())
                .build());
        assertThat(headObjectResponse.metadata())
                .contains(
                        entry("content-type", multipartFile.getContentType()),
                        entry("filename", multipartFile.getOriginalFilename()));
        assertThat(storedObject.readAllBytes()).isEqualTo(Files.readAllBytes(attachmentResource.getFile().toPath()));
    }

    @Test
    void shouldStoreAttachmentWithDefaultContentTypeAndFilenameIfNotAvailable() throws Exception {
        // Given
        GovUkEmailDetailsRequest emailRequest = TestUtil.createValidEmailRequest();
        emailRequest.getSenderDetails().setReference(UUID.randomUUID().toString());

        ClassPathResource attachmentResource = getAttachmentResource("attachment.txt");
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                null, null,
                IOUtils.toByteArray(attachmentResource.getInputStream()));

        // When
        attachmentStorageService.storeAttachment(emailRequest, multipartFile);

        // Then
        HeadObjectResponse headObjectResponse = getHeadeObjectForRequest(emailRequest);
        assertThat(headObjectResponse.metadata())
                .contains(
                        entry("content-type", "application/octet-stream"),
                        entry("filename", multipartFile.getName()));
    }

    private HeadObjectResponse getHeadeObjectForRequest(GovUkEmailDetailsRequest emailRequest) {
        return s3Client.headObject(builder -> builder
                .bucket(s3Mock.getBucket())
                .key(emailRequest.getSenderDetails().getReference())
                .build());
    }

    private ClassPathResource getAttachmentResource(String fileName) {
        return new ClassPathResource("attachments/" + fileName);
    }

    private MultipartFile givenMultipartFileForAttachment(ClassPathResource attachment) throws IOException {
        String contentType = Files.probeContentType(attachment.getFile().toPath());
        return new MockMultipartFile("file",  attachment.getFilename(), contentType, IOUtils.toByteArray(attachment.getInputStream()));
    }
}
