package uk.gov.companieshouse.chs.notification.sender.api.mongo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.chs.notification.sender.api.TestUtil;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.EmailRequestDao;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.LetterRequestDao;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.NotificationEmailRequest;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.NotificationLetterRequest;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.repository.NotificationEmailRequestRepository;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.repository.NotificationLetterRequestRepository;

@ExtendWith(MockitoExtension.class)
class NotificationDatabaseServiceTest {

    @Mock
    NotificationLetterRequestRepository notificationLetterRequestRepository;

    @Mock
    NotificationEmailRequestRepository notificationEmailRequestRepository;

    @InjectMocks
    NotificationDatabaseService notificationDatabaseService;

    @Test
    void testSaveEmail() {
        NotificationEmailRequest request = new NotificationEmailRequest();

        notificationDatabaseService.save(request);

        verify(notificationEmailRequestRepository).save(request);
    }

    @Test
    void testSaveLetter() {
        NotificationLetterRequest request = new NotificationLetterRequest();

        notificationDatabaseService.save(request);

        verify(notificationLetterRequestRepository).save(request);
    }

    @Test
    void getEmailByUniqueReference() {
        String appId = "chips";
        String reference = "TEST";

        EmailRequestDao email = TestUtil.createEmailRequestDao();

        when(notificationEmailRequestRepository.findByUniqueReference(appId, reference))
                .thenReturn(Optional.of(new NotificationEmailRequest(email)));

        Optional<NotificationEmailRequest> retrievedRequest = notificationDatabaseService
                .getEmail(appId, reference);

        assertTrue(retrievedRequest.isPresent());
        assertEquals(email, retrievedRequest.get().getRequest());
        verify(notificationEmailRequestRepository).findByUniqueReference(appId, reference);
    }

    @Test
    void getEmailByUniqueReferenceNotFound() {
        String appId = "chips";
        String reference = "TEST";

        when(notificationEmailRequestRepository.findByUniqueReference(appId, reference))
                .thenReturn(Optional.empty());

        Optional<NotificationEmailRequest> retrievedRequest = notificationDatabaseService
                .getEmail(appId, reference);

        assertFalse(retrievedRequest.isPresent());
        verify(notificationEmailRequestRepository).findByUniqueReference(appId, reference);
    }
    
    @Test
    void getLetterByUniqueReference() {
        String appId = "chips";
        String reference = "TEST";

        LetterRequestDao letter = TestUtil.createLetterRequestDao();

        when(notificationLetterRequestRepository.findByUniqueReference(appId, reference))
                .thenReturn(Optional.of(new NotificationLetterRequest(letter)));

        Optional<NotificationLetterRequest> retrievedRequest = notificationDatabaseService
                .getLetter(appId, reference);

        assertTrue(retrievedRequest.isPresent());
        assertEquals(letter, retrievedRequest.get().getRequest());
        verify(notificationLetterRequestRepository).findByUniqueReference(appId, reference);
    }

    @Test
    void getLetterByUniqueReferenceNotFound() {
        String appId = "chips";
        String reference = "TEST";

        when(notificationLetterRequestRepository.findByUniqueReference(appId, reference))
                .thenReturn(Optional.empty());

        Optional<NotificationLetterRequest> retrievedRequest = notificationDatabaseService
                .getLetter(appId, reference);

        assertFalse(retrievedRequest.isPresent());
        verify(notificationLetterRequestRepository).findByUniqueReference(appId, reference);
    }
}
