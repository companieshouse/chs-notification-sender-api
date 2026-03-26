package uk.gov.companieshouse.chs.notification.sender.api.mongo.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.document.NotificationEmailRequest;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.document.NotificationLetterRequest;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.EmailRequestDao;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.LetterRequestDao;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.repository.NotificationEmailRequestRepository;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.repository.NotificationLetterRequestRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationDatabaseServiceTest {

    @Mock
    NotificationLetterRequestRepository notificationLetterRequestRepository;

    @Mock
    NotificationEmailRequestRepository notificationEmailRequestRepository;

    @InjectMocks
    NotificationDatabaseService notificationDatabaseService;

    @Test
    void testStoreEmail() {
        EmailRequestDao emailRequestDao = new EmailRequestDao();
        when(notificationEmailRequestRepository.save(any(NotificationEmailRequest.class)))
                .thenReturn(new NotificationEmailRequest(null, null, emailRequestDao, null));
        ArgumentCaptor<NotificationEmailRequest> captor = ArgumentCaptor.forClass(NotificationEmailRequest.class);

        notificationDatabaseService.storeEmail(emailRequestDao);

        verify(notificationEmailRequestRepository).save(captor.capture());
        NotificationEmailRequest captured = captor.getValue();
        Assertions.assertEquals(emailRequestDao, captured.getRequest());
    }

    @Test
    void testStoreLetter() {
        LetterRequestDao letterRequestDao = new LetterRequestDao();
        when(notificationLetterRequestRepository.save(any(NotificationLetterRequest.class)))
                .thenReturn(new NotificationLetterRequest(null, null, letterRequestDao, null));
        ArgumentCaptor<NotificationLetterRequest> captor = ArgumentCaptor.forClass(NotificationLetterRequest.class);

        notificationDatabaseService.storeLetter(letterRequestDao);

        verify(notificationLetterRequestRepository).save(captor.capture());
        NotificationLetterRequest captured = captor.getValue();
        Assertions.assertEquals(letterRequestDao, captured.getRequest());
    }
}
