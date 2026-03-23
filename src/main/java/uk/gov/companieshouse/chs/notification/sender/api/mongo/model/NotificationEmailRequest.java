package uk.gov.companieshouse.chs.notification.sender.api.mongo.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "email_details")
public class NotificationEmailRequest extends NotificationRequest<EmailRequestDao> {

    public NotificationEmailRequest() {
        super();
    }

    public NotificationEmailRequest(EmailRequestDao request) {
        this();
        setRequest(request);
    }
}
