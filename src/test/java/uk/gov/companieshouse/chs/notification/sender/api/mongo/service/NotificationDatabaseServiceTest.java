package uk.gov.companieshouse.chs.notification.sender.api.mongo.service;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
}
