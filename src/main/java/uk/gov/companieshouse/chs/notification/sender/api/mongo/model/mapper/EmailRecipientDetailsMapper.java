package uk.gov.companieshouse.chs.notification.sender.api.mongo.model.mapper;

import uk.gov.companieshouse.api.chs.notification.sender.model.RecipientDetailsEmail;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.EmailRecipientDetailsDao;

public class EmailRecipientDetailsMapper {
    private EmailRecipientDetailsMapper() {
        // prevent instantiation
    }

    public static EmailRecipientDetailsDao toDao(RecipientDetailsEmail src) {
        if (src == null) {
            return null;
        }
        EmailRecipientDetailsDao dest = new EmailRecipientDetailsDao();
        dest.setName(src.getName());
        dest.setEmailAddress(src.getEmailAddress());
        return dest;
    }

    public static RecipientDetailsEmail fromDao(EmailRecipientDetailsDao src) {
        if (src == null) {
            return null;
        }
        RecipientDetailsEmail dest = new RecipientDetailsEmail();
        dest.setName(src.getName());
        dest.setEmailAddress(src.getEmailAddress());
        return dest;
    }
}

