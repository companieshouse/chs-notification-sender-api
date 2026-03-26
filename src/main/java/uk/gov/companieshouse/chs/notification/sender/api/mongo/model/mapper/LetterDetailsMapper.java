package uk.gov.companieshouse.chs.notification.sender.api.mongo.model.mapper;

import uk.gov.companieshouse.api.chs.notification.sender.model.LetterDetails;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.LetterDetailsDao;

public class LetterDetailsMapper {
    private LetterDetailsMapper() {
        // prevent instantiation
    }

    public static LetterDetailsDao toDao(LetterDetails src) {
        if (src == null) {
            return null;
        }
        LetterDetailsDao dest = new LetterDetailsDao();
        dest.setTemplateId(src.getTemplateId());
        dest.setPersonalisationDetails(src.getPersonalisationDetails());
        dest.setLetterId(src.getLetterId());
        return dest;
    }

    public static LetterDetails fromDao(LetterDetailsDao src) {
        if (src == null) {
            return null;
        }
        LetterDetails dest = new LetterDetails();
        dest.setTemplateId(src.getTemplateId());
        dest.setPersonalisationDetails(src.getPersonalisationDetails());
        dest.setLetterId(src.getLetterId());
        return dest;
    }
}

