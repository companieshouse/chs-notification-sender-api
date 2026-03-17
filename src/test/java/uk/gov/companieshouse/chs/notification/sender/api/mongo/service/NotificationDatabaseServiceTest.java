package uk.gov.companieshouse.chs.notification.sender.api.mongo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.document.NotificationEmailRequest;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.models.EmailRequestDao;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.models.EmailDetailsDao;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.models.EmailRecipientDetailsDao;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.models.SenderDetailsDao;
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
        // Arrange
        EmailRequestDao emailRequestDao = new EmailRequestDao();
        SenderDetailsDao sender = new SenderDetailsDao();
        sender.setAppId("appId");
        sender.setReference("ref");
        sender.setName("name");
        sender.setUserId("userId");
        sender.setEmailAddress("sender@example.com");
        emailRequestDao.setSenderDetails(sender);

        EmailRecipientDetailsDao recipient = new EmailRecipientDetailsDao();
        recipient.setName("recipient");
        recipient.setEmailAddress("recipient@example.com");
        emailRequestDao.setRecipientDetails(recipient);

        EmailDetailsDao emailDetails = new EmailDetailsDao();
        emailDetails.setTemplateId("templateId");
        emailDetails.setPersonalisationDetails("details");
        emailRequestDao.setEmailDetails(emailDetails);

        NotificationEmailRequest expected = new NotificationEmailRequest(null, null, emailRequestDao, null);
        when(notificationEmailRequestRepository.save(any(NotificationEmailRequest.class))).thenReturn(expected);

        // Act
        notificationDatabaseService.storeEmail(emailRequestDao);

        // Assert
        verify(notificationEmailRequestRepository).save(expected);
    }
}
