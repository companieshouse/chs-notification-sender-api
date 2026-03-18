package uk.gov.companieshouse.chs.notification.sender.api.mongo.models.mapper;

import uk.gov.companieshouse.api.chs.notification.model.RecipientDetailsEmail;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.models.EmailRecipientDetailsDao;

public class EmailRecipientDetailsMapper {
    private EmailRecipientDetailsMapper() {}

    public static EmailRecipientDetailsDao toDao(RecipientDetailsEmail src) {
        if (src == null) return null;
        EmailRecipientDetailsDao dest = new EmailRecipientDetailsDao();
        dest.setName(src.getName());
        dest.setEmailAddress(src.getEmailAddress());
        return dest;
    }

    public static RecipientDetailsEmail fromDao(EmailRecipientDetailsDao src) {
        if (src == null) return null;
        RecipientDetailsEmail dest = new RecipientDetailsEmail();
        dest.setName(src.getName());
        dest.setEmailAddress(src.getEmailAddress());
        return dest;
    }
}

