package uk.gov.companieshouse.chs.notification.sender.api.mongo.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.companieshouse.chs.notification.sender.api.AbstractMongoDBTest;
import uk.gov.companieshouse.chs.notification.sender.api.TestUtil;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.EmailRequestDao;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.NotificationEmailRequest;

@SpringBootTest
class NotificationEmailRequestRepositoryTest extends AbstractMongoDBTest {

    @Test
    void When_NewRequestSaved_Expect_IdAssigned() {
        NotificationEmailRequest notificationEmailRequest = new NotificationEmailRequest();
        notificationEmailRequest.setRequest(TestUtil.createEmailRequestDao());
        notificationEmailRequest.setId(null);
        notificationEmailRequest.setCreatedAt(null);
        notificationEmailRequest.setUpdatedAt(null);
        NotificationEmailRequest savedRequest = notificationEmailRequestRepository.save(notificationEmailRequest);

        assertNotNull(savedRequest.getId());
        assertNotNull(savedRequest.getCreatedAt());
        assertNotNull(savedRequest.getUpdatedAt());
        assertEquals(notificationEmailRequest.getRequest(), savedRequest.getRequest());
    }

    @Test
    void findByUniqueReference() {
        String appId = "chips";
        String otherAppId = "other-app";
        String reference = "TEST";
        var email1 = saveEmailWithReference(appId, reference);
        saveEmailWithReference(otherAppId, reference);

        var result = notificationEmailRequestRepository.findByUniqueReference(appId, reference);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertNotEquals(otherAppId, result.get().getRequest().getSenderDetails().getAppId());
        assertEquals(email1.getEmailDetails(), result.get().getRequest().getEmailDetails());
        assertEquals(email1.getRecipientDetails(), result.get().getRequest().getRecipientDetails());
        assertEquals(email1.getSenderDetails(), result.get().getRequest().getSenderDetails());
    }

    @Test
    void findByUniqueReference_notFound() {
        String reference = "TEST";
        saveEmailWithReference("chips", reference);
        saveEmailWithReference("other-app", reference);

        var result = notificationEmailRequestRepository.findByUniqueReference("app-id", reference);

        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    private EmailRequestDao saveEmailWithReference(String appId, String reference) {
        var email = TestUtil.createEmailRequestDao();
        email.getSenderDetails().setAppId(appId);
        email.getSenderDetails().setReference(reference);
        var request = new NotificationEmailRequest();
        request.setRequest(email);
        notificationEmailRequestRepository.save(request);
        return email;
    }
}
