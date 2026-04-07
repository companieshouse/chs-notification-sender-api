package uk.gov.companieshouse.chs.notification.sender.api.mongo.model.mapper;

import uk.gov.companieshouse.api.chs.notification.sender.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.EmailRequestDao;

public class EmailRequestMapper {
    private EmailRequestMapper() {
        // prevent instantiation
    }
    public static EmailRequestDao toDao(GovUkEmailDetailsRequest src) {
        if (src == null) {
            return null;
        }
        EmailRequestDao dest = new EmailRequestDao();
        dest.setSenderDetails(SenderDetailsMapper.toDao(src.getSenderDetails()));
        dest.setRecipientDetails(EmailRecipientDetailsMapper.toDao(src.getRecipientDetails()));
        dest.setEmailDetails(EmailDetailsMapper.toDao(src.getEmailDetails()));
        dest.setCreatedAt(src.getCreatedAt());
        return dest;
    }
    public static GovUkEmailDetailsRequest fromDao(EmailRequestDao src) {
        if (src == null) {
            return null;
        }
        GovUkEmailDetailsRequest dest = new GovUkEmailDetailsRequest();
        dest.setSenderDetails(SenderDetailsMapper.fromDao(src.getSenderDetails()));
        dest.setRecipientDetails(EmailRecipientDetailsMapper.fromDao(src.getRecipientDetails()));
        dest.setEmailDetails(EmailDetailsMapper.fromDao(src.getEmailDetails()));
        dest.setCreatedAt(src.getCreatedAt());
        return dest;
    }
}
