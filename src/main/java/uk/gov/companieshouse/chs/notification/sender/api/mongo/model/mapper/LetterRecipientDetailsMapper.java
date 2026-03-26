package uk.gov.companieshouse.chs.notification.sender.api.mongo.model.mapper;

import uk.gov.companieshouse.api.chs.notification.sender.model.RecipientDetailsLetter;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.LetterRecipientDetailsDao;

public class LetterRecipientDetailsMapper {
    private LetterRecipientDetailsMapper() {
        // prevent instantiation
    }

    public static LetterRecipientDetailsDao toDao(RecipientDetailsLetter src) {
        if (src == null) {
            return null;
        }
        LetterRecipientDetailsDao dest = new LetterRecipientDetailsDao();
        dest.setName(src.getName());
        dest.setPhysicalAddress(AddressMapper.toDao(src.getPhysicalAddress()));
        return dest;
    }

    public static RecipientDetailsLetter fromDao(LetterRecipientDetailsDao src) {
        if (src == null) {
            return null;
        }
        RecipientDetailsLetter dest = new RecipientDetailsLetter();
        dest.setName(src.getName());
        dest.setPhysicalAddress(AddressMapper.fromDao(src.getPhysicalAddress()));
        return dest;
    }
}

