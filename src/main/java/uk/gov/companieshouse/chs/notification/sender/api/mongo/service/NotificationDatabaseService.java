package uk.gov.companieshouse.chs.notification.sender.api.mongo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.document.NotificationEmailRequest;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.document.NotificationLetterRequest;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.models.EmailRequestDao;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.models.LetterRequestDao;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.repository.NotificationEmailRequestRepository;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.repository.NotificationLetterRequestRepository;


@Service
public class NotificationDatabaseService {

    private final NotificationEmailRequestRepository notificationEmailRequestRepository;
    private final NotificationLetterRequestRepository notificationLetterRequestRepository;

    public NotificationDatabaseService(
            final NotificationEmailRequestRepository notificationEmailRequestRepository,
            final NotificationLetterRequestRepository notificationLetterRequestRepository
    ) {
        this.notificationEmailRequestRepository = notificationEmailRequestRepository;
        this.notificationLetterRequestRepository = notificationLetterRequestRepository;
    }

    @Transactional
    public NotificationEmailRequest storeEmail(final EmailRequestDao emailRequestDao) {
        return notificationEmailRequestRepository.save(new NotificationEmailRequest(null, null, emailRequestDao, null));
    }

    @Transactional
    public NotificationLetterRequest storeLetter(final LetterRequestDao letterRequestDao) {
        return notificationLetterRequestRepository.save(new NotificationLetterRequest(null, null, letterRequestDao, null));
    }

}
