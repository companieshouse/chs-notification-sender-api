package uk.gov.companieshouse.chs.notification.sender.api.mongo.models.mapper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.chs.notification.model.RecipientDetailsEmail;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.EmailRecipientDetailsDao;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.mapper.EmailRecipientDetailsMapper;

class EmailRecipientDetailsMapperTest {
    @Test
    void toDaoFromDao() {
        RecipientDetailsEmail recipient = new RecipientDetailsEmail();
        recipient.setName("Recipient Name");
        recipient.setEmailAddress("recipient@example.com");

        EmailRecipientDetailsDao dao = EmailRecipientDetailsMapper.toDao(recipient);
        assertNotNull(dao);
        assertEquals(recipient.getName(), dao.getName());
        assertEquals(recipient.getEmailAddress(), dao.getEmailAddress());

        RecipientDetailsEmail mappedBack = EmailRecipientDetailsMapper.fromDao(dao);
        assertNotNull(mappedBack);
        assertEquals(recipient, mappedBack);
    }

    @Test
    void toDaoNull() {
        RecipientDetailsEmail recipient = null;
        assertNull(EmailRecipientDetailsMapper.toDao(recipient));
    }

    @Test
    void fromDaoNull() {
        EmailRecipientDetailsDao dao = null;
        assertNull(EmailRecipientDetailsMapper.fromDao(dao));
    }
}

