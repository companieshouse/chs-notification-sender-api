package uk.gov.companieshouse.chs.notification.sender.api;

import java.time.OffsetDateTime;
import uk.gov.companieshouse.api.chs.notification.sender.model.Address;
import uk.gov.companieshouse.api.chs.notification.sender.model.EmailDetails;
import uk.gov.companieshouse.api.chs.notification.sender.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.api.chs.notification.sender.model.GovUkLetterDetailsRequest;
import uk.gov.companieshouse.api.chs.notification.sender.model.LetterDetails;
import uk.gov.companieshouse.api.chs.notification.sender.model.RecipientDetailsEmail;
import uk.gov.companieshouse.api.chs.notification.sender.model.RecipientDetailsLetter;
import uk.gov.companieshouse.api.chs.notification.sender.model.SenderDetails;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.AddressDao;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.EmailDetailsDao;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.EmailRecipientDetailsDao;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.EmailRequestDao;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.LetterDetailsDao;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.LetterRecipientDetailsDao;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.LetterRequestDao;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.SenderDetailsDao;

public class TestUtil {
    private static final String DEFAULT_APP_ID = "test-app-id";
    private static final String DEFAULT_REFERENCE = "test-reference";
    private static final String DEFAULT_SENDER_NAME = "Sender Name";
    private static final String DEFAULT_USER_ID = "test-user-123";
    private static final String DEFAULT_SENDER_EMAIL = "sender@example.com";
    private static final String DEFAULT_RECIPIENT_NAME = "Recipient Name";
    private static final String DEFAULT_RECIPIENT_EMAIL = "recipient@example.com";
    private static final String DEFAULT_EMAIL_TEMPLATE_ID = "template-uuid-email-12345678";
    private static final String DEFAULT_LETTER_TEMPLATE_ID = "template-uuid-letter-87654321";
    private static final String DEFAULT_LETTER_ID = "LETTER_TYPE_ID";
    private static final String DEFAULT_EMAIL_CONTENT = "{\"subject\":\"Test Subject\",\"content\":\"This is a test email\"}";
    private static final String DEFAULT_LETTER_CONTENT = "{\"subject\":\"Test Letter\",\"content\":\"This is a test letter\"}";

    private static final String DEFAULT_ADDRESS_LINE_1 = "123 Test Street";
    private static final String DEFAULT_ADDRESS_LINE_2 = "Apartment 101";
    private static final String DEFAULT_ADDRESS_LINE_3 = "Test District";
    private static final String DEFAULT_ADDRESS_LINE_4 = "Test City";
    private static final String DEFAULT_ADDRESS_LINE_5 = "Test County";
    private static final String DEFAULT_ADDRESS_LINE_6 = "TE5 7ST";
    private static final String DEFAULT_ADDRESS_LINE_7 = "United Kingdom";

    public static GovUkEmailDetailsRequest createValidEmailRequest() {
        return new GovUkEmailDetailsRequest()
                .senderDetails(createDefaultSenderDetails())
                .recipientDetails(createDefaultRecipientDetailsEmail())
                .emailDetails(createDefaultEmailDetails())
                .createdAt(OffsetDateTime.now());
    }

    public static GovUkLetterDetailsRequest createValidLetterRequest() {
        return new GovUkLetterDetailsRequest()
                .senderDetails(createDefaultSenderDetails())
                .recipientDetails(createDefaultRecipientDetailsLetter())
                .letterDetails(createDefaultLetterDetails())
                .createdAt(OffsetDateTime.now());
    }

    public static SenderDetails createDefaultSenderDetails() {
        return new SenderDetails(DEFAULT_APP_ID, DEFAULT_REFERENCE)
                .name(DEFAULT_SENDER_NAME)
                .userId(DEFAULT_USER_ID)
                .emailAddress(DEFAULT_SENDER_EMAIL);
    }

    public static RecipientDetailsEmail createDefaultRecipientDetailsEmail() {
        return new RecipientDetailsEmail(DEFAULT_RECIPIENT_NAME, DEFAULT_RECIPIENT_EMAIL);
    }

    public static EmailDetails createDefaultEmailDetails() {
        return new EmailDetails(DEFAULT_EMAIL_TEMPLATE_ID, DEFAULT_EMAIL_CONTENT);
    }

    public static RecipientDetailsLetter createDefaultRecipientDetailsLetter() {
        return new RecipientDetailsLetter(DEFAULT_RECIPIENT_NAME, createDefaultAddress());
    }

    public static LetterDetails createDefaultLetterDetails() {
        return new LetterDetails(DEFAULT_LETTER_TEMPLATE_ID, DEFAULT_LETTER_CONTENT).letterId(DEFAULT_LETTER_ID);
    }

    public static Address createDefaultAddress() {
        return new Address()
                .addressLine1(DEFAULT_ADDRESS_LINE_1)
                .addressLine2(DEFAULT_ADDRESS_LINE_2)
                .addressLine3(DEFAULT_ADDRESS_LINE_3)
                .addressLine4(DEFAULT_ADDRESS_LINE_4)
                .addressLine5(DEFAULT_ADDRESS_LINE_5)
                .addressLine6(DEFAULT_ADDRESS_LINE_6)
                .addressLine7(DEFAULT_ADDRESS_LINE_7);
    }

    public static GovUkEmailDetailsRequest createEmailRequestWithCustomEmail(String email) {
        return createValidEmailRequest().recipientDetails(new RecipientDetailsEmail(DEFAULT_RECIPIENT_NAME, email));
    }

    public static LetterRequestDao createLetterRequestDao() {
        SenderDetailsDao senderDetails = new SenderDetailsDao();
        senderDetails.setAppId(DEFAULT_APP_ID);
        senderDetails.setReference(DEFAULT_REFERENCE);
        AddressDao address = new AddressDao();
        address.setAddressLine1(DEFAULT_ADDRESS_LINE_1);
        address.setAddressLine2(DEFAULT_ADDRESS_LINE_2);
        address.setAddressLine3(DEFAULT_ADDRESS_LINE_3);
        address.setAddressLine4(DEFAULT_ADDRESS_LINE_4);
        address.setAddressLine5(DEFAULT_ADDRESS_LINE_5);
        address.setAddressLine6(DEFAULT_ADDRESS_LINE_6);
        address.setAddressLine7(DEFAULT_ADDRESS_LINE_7);
        LetterRecipientDetailsDao recipientDetails = new LetterRecipientDetailsDao();
        recipientDetails.setName(DEFAULT_RECIPIENT_NAME);
        recipientDetails.setPhysicalAddress(address);
        LetterDetailsDao letterDetails = new LetterDetailsDao();
        letterDetails.setLetterId(DEFAULT_LETTER_ID);
        letterDetails.setTemplateId(DEFAULT_LETTER_TEMPLATE_ID);
        letterDetails.setPersonalisationDetails(DEFAULT_LETTER_CONTENT);

        LetterRequestDao letterRequest = new LetterRequestDao();
        letterRequest.setSenderDetails(senderDetails);
        letterRequest.setRecipientDetails(recipientDetails);
        letterRequest.setLetterDetails(letterDetails);
        letterRequest.setCreatedAt(OffsetDateTime.now());
        return letterRequest;
    }

    public static EmailRequestDao createEmailRequestDao() {
        SenderDetailsDao senderDetails = new SenderDetailsDao();
        senderDetails.setAppId(DEFAULT_APP_ID);
        senderDetails.setReference(DEFAULT_REFERENCE);
        EmailRecipientDetailsDao recipientDetails = new EmailRecipientDetailsDao();
        recipientDetails.setName(DEFAULT_RECIPIENT_NAME);
        recipientDetails.setEmailAddress(DEFAULT_RECIPIENT_EMAIL);
        EmailDetailsDao emailDetails = new EmailDetailsDao();
        emailDetails.setTemplateId(DEFAULT_EMAIL_TEMPLATE_ID);
        emailDetails.setPersonalisationDetails(DEFAULT_EMAIL_CONTENT);

        EmailRequestDao emailRequest = new EmailRequestDao();
        emailRequest.setSenderDetails(senderDetails);
        emailRequest.setRecipientDetails(recipientDetails);
        emailRequest.setEmailDetails(emailDetails);
        emailRequest.setCreatedAt(OffsetDateTime.now());
        return emailRequest;
    }
}
