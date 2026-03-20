package uk.gov.companieshouse.chs.notification.sender.api.mongo.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.companieshouse.api.chs.notification.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.chs.notification.sender.api.AbstractMongoDBTest;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.document.NotificationEmailRequest;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.models.mapper.EmailRequestMapper;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.companieshouse.chs.notification.sender.api.TestUtil.createEmailRequestWithCustomEmail;

@SpringBootTest
class NotificationEmailRequestRepositoryTest extends AbstractMongoDBTest {

    @Autowired
    private NotificationEmailRequestRepository requestRepository;

    @Test
    void When_NewRequestSaved_Expect_IdAssigned() {
        GovUkEmailDetailsRequest emailRequest = createEmailRequestWithCustomEmail("john.doe@example.com");
        NotificationEmailRequest notificationEmailRequest = new NotificationEmailRequest();
        notificationEmailRequest.setRequest(EmailRequestMapper.toDao(emailRequest));
        notificationEmailRequest.setId(null);
        notificationEmailRequest.setCreatedAt(null);
        notificationEmailRequest.setUpdatedAt(null);
        NotificationEmailRequest savedRequest = requestRepository.save(notificationEmailRequest);

        assertNotNull(savedRequest.toString());
        assertNotNull(savedRequest.getId());
        assertNotNull(savedRequest.getCreatedAt());
        assertNotNull(savedRequest.getUpdatedAt());
    }

}
