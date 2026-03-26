package uk.gov.companieshouse.chs.notification.sender.api.mongo.service;

import java.util.Optional;
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
    
    @Transactional( readOnly = true )
    public Optional<NotificationEmailRequest> getEmail(final String appId, final String reference) {
        return notificationEmailRequestRepository.findByUniqueReference(appId, reference);
    }

    @Transactional( readOnly = true )
    public Optional<NotificationLetterRequest> getLetter(final String appId, final String reference) {
        return notificationLetterRequestRepository.findByUniqueReference(appId, reference);
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
