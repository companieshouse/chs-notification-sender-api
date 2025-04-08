package uk.gov.companieshouse.chs.notification.sender.api;


import java.math.BigDecimal;
import java.time.OffsetDateTime;

import uk.gov.companieshouse.api.chs_notification_sender.model.Address;
import uk.gov.companieshouse.api.chs_notification_sender.model.EmailDetails;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkLetterDetailsRequest;
import uk.gov.companieshouse.api.chs_notification_sender.model.LetterDetails;
import uk.gov.companieshouse.api.chs_notification_sender.model.RecipientDetailsEmail;
import uk.gov.companieshouse.api.chs_notification_sender.model.RecipientDetailsLetter;
import uk.gov.companieshouse.api.chs_notification_sender.model.SenderDetails;

public class TestUtil {
    private static final String DEFAULT_APP_ID = "test-app-id";
    private static final String DEFAULT_REFERENCE = "test-reference";
    private static final String DEFAULT_SENDER_NAME = "Michael";
    private static final String DEFAULT_USER_ID = "userId";
    private static final String DEFAULT_SENDER_EMAIL = "test@example.com";

    private static final String DEFAULT_RECIPIENT_NAME = "Test User";
    private static final String DEFAULT_RECIPIENT_EMAIL = "test@example.com";

    private static final String DEFAULT_EMAIL_TEMPLATE_ID = "template-123";
    private static final String DEFAULT_LETTER_TEMPLATE_ID = "template-456";
    private static final BigDecimal DEFAULT_TEMPLATE_VERSION = new BigDecimal("1.0");

    public static final String DEFAULT_EMAIL_CONTENT = "{\"name\":\"Test User\"}";
    public static final String DEFAULT_LETTER_CONTENT = "{\"name\":\"Test User\"}";

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
        return new EmailDetails(DEFAULT_EMAIL_TEMPLATE_ID, DEFAULT_TEMPLATE_VERSION, DEFAULT_EMAIL_CONTENT);
    }
    
    public static RecipientDetailsLetter createDefaultRecipientDetailsLetter() {
        return new RecipientDetailsLetter(DEFAULT_RECIPIENT_NAME, createDefaultAddress());
    }

    public static LetterDetails createDefaultLetterDetails() {
        return new LetterDetails(DEFAULT_LETTER_TEMPLATE_ID, DEFAULT_TEMPLATE_VERSION, DEFAULT_LETTER_CONTENT);
    }

    public static Address createDefaultAddress() {
        return new Address()
                .addressLine1("123 Test St")
                .addressLine2("Apt 101")
                .addressLine3("District")
                .addressLine4("City")
                .addressLine5("County")
                .addressLine6("Postcode")
                .addressLine7("UK");
    }

    public static GovUkEmailDetailsRequest createEmailRequestWithCustomEmail(String email) {
        return createValidEmailRequest().recipientDetails(new RecipientDetailsEmail(DEFAULT_RECIPIENT_NAME, email));
    }
    

}
