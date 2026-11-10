package uk.gov.companieshouse.chs.notification.sender.api;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.repository.NotificationEmailRequestRepository;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.repository.NotificationLetterRequestRepository;

import java.time.Duration;

public abstract class AbstractMongoDBTest {

    private static final DockerImageName MONGO_IMAGE = DockerImageName.parse("mongo:8.0.34");

    @ServiceConnection
    protected static final MongoDBContainer mongoDBContainer =
            new MongoDBContainer(MONGO_IMAGE)
                    .withStartupTimeout(Duration.ofMinutes(2));

    static {
        mongoDBContainer.start();
        TestUtil.configureJsonPathDefaults();
    }

    @Autowired
    protected NotificationLetterRequestRepository notificationLetterRequestRepository;

    @Autowired
    protected NotificationEmailRequestRepository notificationEmailRequestRepository;

    @DynamicPropertySource
    static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @AfterEach
    void tearDown() {
        notificationLetterRequestRepository.deleteAll();
        notificationEmailRequestRepository.deleteAll();
    }
}
