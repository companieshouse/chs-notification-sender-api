package uk.gov.companieshouse.chs.notification.sender.api.mapper;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.companieshouse.api.chs.notification.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.api.chs.notification.model.GovUkLetterDetailsRequest;
import uk.gov.companieshouse.chs.notification.sender.api.TestUtil;
import uk.gov.companieshouse.notification.ChsEmailNotification;
import uk.gov.companieshouse.notification.ChsLetterNotification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class NotificationMapperTest {

    @Autowired
    private NotificationMapper notificationMapper;

    @Test
    void When_MapEmailRequest_Expect_CorrectChsEmailNotification() {
        GovUkEmailDetailsRequest request = TestUtil.createValidEmailRequest();

        ChsEmailNotification result = notificationMapper.mapToEmailDetailsRequest(request);

        assertNotNull(result);
        assertNotNull(result.getSenderDetails());
        assertEquals(TestUtil.DEFAULT_APP_ID, result.getSenderDetails().getAppId());
        assertEquals(TestUtil.DEFAULT_REFERENCE, result.getSenderDetails().getReference());
        assertEquals(TestUtil.DEFAULT_SENDER_NAME, result.getSenderDetails().getName());
        assertEquals(TestUtil.DEFAULT_USER_ID, result.getSenderDetails().getUserId());
        assertEquals(TestUtil.DEFAULT_SENDER_EMAIL, result.getSenderDetails().getEmailAddress());
        assertNotNull(result.getRecipientDetails());
        assertEquals(TestUtil.DEFAULT_RECIPIENT_NAME, result.getRecipientDetails().getName());
        assertEquals(TestUtil.DEFAULT_RECIPIENT_EMAIL, result.getRecipientDetails().getEmailAddress());
        assertNotNull(result.getEmailDetails());
        assertEquals(TestUtil.DEFAULT_EMAIL_TEMPLATE_ID, result.getEmailDetails().getTemplateId());
        assertEquals(TestUtil.DEFAULT_TEMPLATE_VERSION_DOUBLE, result.getEmailDetails().getTemplateVersion());
        assertEquals(TestUtil.DEFAULT_EMAIL_CONTENT, result.getEmailDetails().getPersonalisationDetails());
        assertEquals(
                request.getCreatedAt().toString(),
                result.getCreatedAt()
        );
    }

    @Test
    void When_MapLetterRequest_Expect_CorrectChsLetterNotification() {
        GovUkLetterDetailsRequest request = TestUtil.createValidLetterRequest();

        ChsLetterNotification result = notificationMapper.mapToLetterDetailsRequest(request);

        assertNotNull(result);
        assertNotNull(result.getSenderDetails());
        assertEquals(TestUtil.DEFAULT_APP_ID, result.getSenderDetails().getAppId());
        assertEquals(TestUtil.DEFAULT_REFERENCE, result.getSenderDetails().getReference());
        assertEquals(TestUtil.DEFAULT_SENDER_NAME, result.getSenderDetails().getName());
        assertEquals(TestUtil.DEFAULT_USER_ID, result.getSenderDetails().getUserId());
        assertNotNull(result.getRecipientDetails());
        assertEquals(TestUtil.DEFAULT_RECIPIENT_NAME, result.getRecipientDetails().getName());
        assertNotNull(result.getRecipientDetails().getPhysicalAddress());
        assertEquals(TestUtil.DEFAULT_ADDRESS_LINE_1, result.getRecipientDetails().getPhysicalAddress().getAddressLine1());
        assertEquals(TestUtil.DEFAULT_ADDRESS_LINE_2, result.getRecipientDetails().getPhysicalAddress().getAddressLine2());
        assertEquals(TestUtil.DEFAULT_ADDRESS_LINE_3, result.getRecipientDetails().getPhysicalAddress().getAddressLine3());
        assertEquals(TestUtil.DEFAULT_ADDRESS_LINE_4, result.getRecipientDetails().getPhysicalAddress().getAddressLine4());
        assertEquals(TestUtil.DEFAULT_ADDRESS_LINE_5, result.getRecipientDetails().getPhysicalAddress().getAddressLine5());
        assertEquals(TestUtil.DEFAULT_ADDRESS_LINE_6, result.getRecipientDetails().getPhysicalAddress().getAddressLine6());
        assertEquals(TestUtil.DEFAULT_ADDRESS_LINE_7, result.getRecipientDetails().getPhysicalAddress().getAddressLine7());
        assertNotNull(result.getLetterDetails());
        assertEquals(TestUtil.DEFAULT_LETTER_TEMPLATE_ID, result.getLetterDetails().getTemplateId());
        assertEquals(TestUtil.DEFAULT_TEMPLATE_VERSION_DOUBLE, result.getLetterDetails().getTemplateVersion());
        assertEquals(TestUtil.DEFAULT_LETTER_CONTENT, result.getLetterDetails().getPersonalisationDetails());
        assertEquals(
                request.getCreatedAt().toString(),
                result.getCreatedAt()
        );
    }

    @Test
    void When_OffsetDateTimeToInstantMethodCalled_Expect_CorrectConversion() {
        OffsetDateTime dateTime = OffsetDateTime.parse("2023-01-01T12:00:00Z");

        String result = NotificationMapper.offsetDateTimeToString(dateTime);

        assertEquals("2023-01-01T12:00Z", result);
    }

    @Test
    void When_OffsetDateTimeToInstantCalledWithNull_Expect_Null() {
        assertNull(NotificationMapper.offsetDateTimeToString(null));
    }
}
