package uk.gov.companieshouse.chs.notification.sender.api.mongo.models.mapper;

import uk.gov.companieshouse.api.chs.notification.model.SenderDetails;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.models.SenderDetailsDao;

public class SenderDetailsMapper {
    private SenderDetailsMapper() {}

    public static SenderDetailsDao toDao(SenderDetails src) {
        if (src == null) return null;
        SenderDetailsDao dest = new SenderDetailsDao();
        dest.setAppId(src.getAppId());
        dest.setReference(src.getReference());
        dest.setName(src.getName());
        dest.setUserId(src.getUserId());
        dest.setEmailAddress(src.getEmailAddress());
        return dest;
    }

    public static SenderDetails fromDao(SenderDetailsDao src) {
        if (src == null) return null;
        SenderDetails dest = new SenderDetails();
        dest.setAppId(src.getAppId());
        dest.setReference(src.getReference());
        dest.setName(src.getName());
        dest.setUserId(src.getUserId());
        dest.setEmailAddress(src.getEmailAddress());
        return dest;
    }
}

