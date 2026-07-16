package uk.gov.companieshouse.chs.notification.sender.api.storage;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import uk.gov.companieshouse.api.chs.notification.sender.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.chs.notification.sender.api.config.AwsProperties;

@Service
public class AttachmentStorageService {

    private final S3Client s3Client;
    private final AwsProperties awsProperties;

    public AttachmentStorageService(S3Client s3Client,
                                    AwsProperties awsProperties) {
        this.s3Client = s3Client;
        this.awsProperties = awsProperties;
    }

    public String storeAttachment(GovUkEmailDetailsRequest emailDetailsRequest,
                                  MultipartFile attachment) throws IOException {
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(awsProperties.bucketName())
                        .key(emailDetailsRequest.getSenderDetails().getReference())
                        .metadata(Map.of(
                                "Content-Type", "application/pdf",
                                "filename", attachment.getName()))
                        .build(),
                RequestBody.fromBytes(attachment.getBytes()));
        return emailDetailsRequest.getSenderDetails().getReference();
    }
}
