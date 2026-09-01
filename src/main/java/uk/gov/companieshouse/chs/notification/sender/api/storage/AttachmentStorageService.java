package uk.gov.companieshouse.chs.notification.sender.api.storage;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import uk.gov.companieshouse.api.chs.notification.sender.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.chs.notification.sender.api.config.AwsProperties;
import uk.gov.companieshouse.chs.notification.sender.api.exception.NotificationException;

@Service
public class AttachmentStorageService {

    public static final String CONTENT_TYPE = "content-type";
    public static final String FILENAME = "filename";
    private final S3Client s3Client;
    private final AwsProperties awsProperties;

    public AttachmentStorageService(S3Client s3Client,
                                    AwsProperties awsProperties) {
        this.s3Client = s3Client;
        this.awsProperties = awsProperties;
    }

    public String storeAttachment(GovUkEmailDetailsRequest emailDetailsRequest,
                                  MultipartFile attachment) {
        try {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(awsProperties.bucketName())
                            .key(emailDetailsRequest.getSenderDetails().getReference())
                            .contentEncoding(attachment.getContentType())
                            .metadata(Map.of(
                                    CONTENT_TYPE, StringUtils.defaultIfBlank(attachment.getContentType(), "application/octet-stream"),
                                    FILENAME, StringUtils.defaultIfBlank(attachment.getOriginalFilename(), attachment.getName())))
                            .build(),
                    RequestBody.fromBytes(attachment.getBytes()));
        } catch (IOException e) {
            throw new NotificationException("Failed to store attachment", e);
        }
        return emailDetailsRequest.getSenderDetails().getReference();
    }
}
