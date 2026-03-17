package uk.gov.companieshouse.chs.notification.sender.api.mongo.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.companieshouse.api.chs.notification.model.GovUkLetterDetailsRequest;
import uk.gov.companieshouse.chs.notification.sender.api.AbstractMongoDBTest;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.document.NotificationLetterRequest;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.models.LetterRequestMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.companieshouse.chs.notification.sender.api.TestUtil.createValidLetterRequest;

@SpringBootTest
class NotificationLetterRequestRepositoryTest extends AbstractMongoDBTest {

    @Autowired
    private NotificationLetterRequestRepository requestRepository;

    @Test
    void When_NewRequestSaved_Expect_IdAssigned() {
        GovUkLetterDetailsRequest letterRequest = createValidLetterRequest();
        letterRequest.getRecipientDetails().getPhysicalAddress().setAddressLine1("123 Main St");
        NotificationLetterRequest notificationLetterRequest = new NotificationLetterRequest();
        notificationLetterRequest.setRequest(LetterRequestMapper.toDao(letterRequest));
        notificationLetterRequest.setId(null);
        notificationLetterRequest.setCreatedAt(null);
        notificationLetterRequest.setUpdatedAt(null);
        NotificationLetterRequest savedRequest = requestRepository.save(notificationLetterRequest);

        assertNotNull(savedRequest.toString());
        assertNotNull(savedRequest.getId());
        assertNotNull(savedRequest.getCreatedAt());
        assertNotNull(savedRequest.getUpdatedAt());
    }

    @Test
    void When_RequestUpdated_Expect_ChangesReflectedInDatabase() {
        GovUkLetterDetailsRequest initialRequest = createValidLetterRequest();
        initialRequest.getRecipientDetails().getPhysicalAddress().setAddressLine1("Initial Address");

        NotificationLetterRequest savedRequest = requestRepository.save(new NotificationLetterRequest(null, null, LetterRequestMapper.toDao(initialRequest), null));

        savedRequest.getRequest().getRecipientDetails().getPhysicalAddress().setAddressLine1( "Updated Address" );

        NotificationLetterRequest updatedRequest = requestRepository.save(savedRequest);

        assertNotNull(updatedRequest);
        assertEquals("Updated Address", updatedRequest.getRequest().getRecipientDetails().getPhysicalAddress().getAddressLine1());
    }


}
