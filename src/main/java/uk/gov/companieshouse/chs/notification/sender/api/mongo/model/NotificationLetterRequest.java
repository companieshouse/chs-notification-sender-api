package uk.gov.companieshouse.chs.notification.sender.api.mongo.model;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "letter_details")
@CompoundIndex(
        name = "sender_app_id_reference",
        def = "{'request.sender_details.app_id': 1, 'request.sender_details.reference': 1}"
)
public class NotificationLetterRequest extends NotificationRequest<LetterRequestDao> {

    public NotificationLetterRequest() {
        super();
    }

    public NotificationLetterRequest(LetterRequestDao request) {
        this();
        setRequest(request);
    }
}
