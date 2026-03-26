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
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.LetterRequestDao;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.NotificationLetterRequest;

@SpringBootTest
class NotificationLetterRequestRepositoryTest extends AbstractMongoDBTest {

    @Test
    void When_NewRequestSaved_Expect_IdAssigned() {
        NotificationLetterRequest notificationLetterRequest = new NotificationLetterRequest();
        notificationLetterRequest.setRequest(TestUtil.createLetterRequestDao());
        NotificationLetterRequest savedRequest = notificationLetterRequestRepository.save(notificationLetterRequest);

        assertNotNull(savedRequest.getId());
        assertNotNull(savedRequest.getCreatedAt());
        assertNotNull(savedRequest.getUpdatedAt());
        assertEquals(notificationLetterRequest.getRequest(), savedRequest.getRequest());
    }

    @Test
    void When_RequestUpdated_Expect_ChangesReflectedInDatabase() {
        NotificationLetterRequest request = new NotificationLetterRequest();
        request.setRequest(TestUtil.createLetterRequestDao());
        NotificationLetterRequest savedRequest = notificationLetterRequestRepository.save(request);

        savedRequest.getRequest().getRecipientDetails().getPhysicalAddress().setAddressLine1( "Updated Address" );

        NotificationLetterRequest updatedRequest = notificationLetterRequestRepository.save(savedRequest);

        assertNotNull(updatedRequest);
        assertEquals("Updated Address", updatedRequest.getRequest().getRecipientDetails().getPhysicalAddress().getAddressLine1());
    }

    @Test
    void findByUniqueReference() {
        String appId = "chips";
        String otherAppId = "other-app";
        String reference = "TEST";
        var letter1 = saveLetterWithReference(appId, reference);
        saveLetterWithReference(otherAppId, reference);

        var result = notificationLetterRequestRepository.findByUniqueReference(appId, reference);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertNotEquals(otherAppId, result.get().getRequest().getSenderDetails().getAppId());
        assertEquals(letter1.getLetterDetails(), result.get().getRequest().getLetterDetails());
        assertEquals(letter1.getRecipientDetails(), result.get().getRequest().getRecipientDetails());
        assertEquals(letter1.getSenderDetails(), result.get().getRequest().getSenderDetails());
    }

    @Test
    void findByUniqueReference_notFound() {
        String reference = "TEST";
        saveLetterWithReference("chips", reference);
        saveLetterWithReference("other-app", reference);

        var result = notificationLetterRequestRepository.findByUniqueReference("app-id", reference);

        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    private LetterRequestDao saveLetterWithReference(String appId, String reference) {
        var letter = TestUtil.createLetterRequestDao();
        letter.getSenderDetails().setAppId(appId);
        letter.getSenderDetails().setReference(reference);
        notificationLetterRequestRepository.save(new NotificationLetterRequest(letter));
        return letter;
    }

}
