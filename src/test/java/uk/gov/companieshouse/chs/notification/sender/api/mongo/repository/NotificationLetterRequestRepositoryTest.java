package uk.gov.companieshouse.chs.notification.sender.api.mongo.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.companieshouse.api.chs.notification.model.GovUkLetterDetailsRequest;
import uk.gov.companieshouse.chs.notification.sender.api.AbstractMongoDBTest;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.NotificationLetterRequest;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.mapper.LetterRequestMapper;

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

        NotificationLetterRequest request = new NotificationLetterRequest();
        request.setRequest(LetterRequestMapper.toDao(initialRequest));
        NotificationLetterRequest savedRequest = requestRepository.save(request);

        savedRequest.getRequest().getRecipientDetails().getPhysicalAddress().setAddressLine1( "Updated Address" );

        NotificationLetterRequest updatedRequest = requestRepository.save(savedRequest);

        assertNotNull(updatedRequest);
        assertEquals("Updated Address", updatedRequest.getRequest().getRecipientDetails().getPhysicalAddress().getAddressLine1());
    }


}
