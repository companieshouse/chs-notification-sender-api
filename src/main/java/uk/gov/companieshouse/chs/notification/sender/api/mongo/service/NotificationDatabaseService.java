package uk.gov.companieshouse.chs.notification.sender.api.mongo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.NotificationEmailRequest;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.NotificationLetterRequest;
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
    public NotificationEmailRequest save(final NotificationEmailRequest request) {
        return notificationEmailRequestRepository.save(request);
    }

    @Transactional
    public NotificationLetterRequest save(final NotificationLetterRequest request) {
        return notificationLetterRequestRepository.save(request);
    }
}
