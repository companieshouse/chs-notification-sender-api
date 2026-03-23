package uk.gov.companieshouse.chs.notification.sender.api.mongo.model.mapper;

import uk.gov.companieshouse.api.chs.notification.model.EmailDetails;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.EmailDetailsDao;

public class EmailDetailsMapper {
    private EmailDetailsMapper() {
        // prevent instantiation
    }

    public static EmailDetailsDao toDao(EmailDetails src) {
        if (src == null) {
            return null;
        }
        EmailDetailsDao dest = new EmailDetailsDao();
        dest.setTemplateId(src.getTemplateId());
        dest.setPersonalisationDetails(src.getPersonalisationDetails());
        return dest;
    }

    public static EmailDetails fromDao(EmailDetailsDao src) {
        if (src == null) {
            return null;
        }
        EmailDetails dest = new EmailDetails();
        dest.setTemplateId(src.getTemplateId());
        dest.setPersonalisationDetails(src.getPersonalisationDetails());
        return dest;
    }
}

