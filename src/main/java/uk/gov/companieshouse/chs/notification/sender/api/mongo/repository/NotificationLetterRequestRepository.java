package uk.gov.companieshouse.chs.notification.sender.api.mongo.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.NotificationLetterRequest;

@Repository
public interface NotificationLetterRequestRepository
        extends MongoRepository<NotificationLetterRequest, String> {

    @Query("{ 'request.sender_details.app_id' : ?0, 'request.sender_details.reference' : ?1 }")
    Optional<NotificationLetterRequest> findByUniqueReference(String appId, String reference);

}
