package uk.gov.companieshouse.chs.notification.sender.api.config;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chs.notification.aws")
public record AwsProperties(URI s3Endpoint,
                            String region,
                            String bucketName,
                            String accessKeyId,
                            String secretAccessKey,
                            Boolean pathStyleAccessEnabled) {

}
