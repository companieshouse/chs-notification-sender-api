package uk.gov.companieshouse.chs.notification.sender.api.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.NotificationLetterRequest;

@Repository
public interface NotificationLetterRequestRepository extends
        MongoRepository<NotificationLetterRequest, String> {
}
