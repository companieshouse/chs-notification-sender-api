package uk.gov.companieshouse.chs.notification.sender.api.mapper;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkLetterDetailsRequest;
import uk.gov.companieshouse.chs.notification.sender.api.TestUtil;
import uk.gov.companieshouse.notification.ChsEmailNotification;
import uk.gov.companieshouse.notification.ChsLetterNotification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class NotificationMapperTest {

    @Autowired
    private NotificationMapper notificationMapper;

    @Test
    public void When_MapEmailRequest_Expect_CorrectChsEmailNotification() {
        GovUkEmailDetailsRequest request = TestUtil.createValidEmailRequest();
        
        ChsEmailNotification result = notificationMapper.mapToEmailDetailsRequest(request);

        assertNotNull(result);
        assertNotNull(result.getSenderDetails());
        assertEquals("test-app-id", result.getSenderDetails().getAppId());
        assertEquals("test-reference", result.getSenderDetails().getReference());
        assertEquals("Michael", result.getSenderDetails().getName());
        assertEquals("userId", result.getSenderDetails().getUserId());
        assertEquals("test@example.com", result.getSenderDetails().getEmailAddress());
        assertNotNull(result.getRecipientDetails());
        assertEquals("Test User", result.getRecipientDetails().getName());
        assertEquals("test@example.com", result.getRecipientDetails().getEmailAddress());
        assertNotNull(result.getEmailDetails());
        assertEquals("template-123", result.getEmailDetails().getTemplateId());
        assertEquals("1.0", result.getEmailDetails().getTemplateVersion());
        assertEquals(TestUtil.DEFAULT_EMAIL_CONTENT, result.getEmailDetails().getPersonalisationDetails());
        assertEquals(
                request.getCreatedAt().toInstant().truncatedTo(ChronoUnit.MILLIS),
                result.getCreatedAt().truncatedTo(ChronoUnit.MILLIS)
        );
    }

    @Test
    public void When_MapLetterRequest_Expect_CorrectChsLetterNotification() {
        GovUkLetterDetailsRequest request = TestUtil.createValidLetterRequest();

        ChsLetterNotification result = notificationMapper.mapToLetterDetailsRequest(request);

        assertNotNull(result);
        assertNotNull(result.getSenderDetails());
        assertEquals("test-app-id", result.getSenderDetails().getAppId());
        assertEquals("test-reference", result.getSenderDetails().getReference());
        assertEquals("Michael", result.getSenderDetails().getName());
        assertEquals("userId", result.getSenderDetails().getUserId());
        assertNotNull(result.getRecipientDetails());
        assertEquals("Test User", result.getRecipientDetails().getName());
        assertNotNull(result.getRecipientDetails().getPhysicalAddress());
        assertEquals("123 Test St", result.getRecipientDetails().getPhysicalAddress().getAddressLine1());
        assertEquals("Apt 101", result.getRecipientDetails().getPhysicalAddress().getAddressLine2());
        assertEquals("District", result.getRecipientDetails().getPhysicalAddress().getAddressLine3());
        assertEquals("City", result.getRecipientDetails().getPhysicalAddress().getAddressLine4());
        assertEquals("County", result.getRecipientDetails().getPhysicalAddress().getAddressLine5());
        assertEquals("Postcode", result.getRecipientDetails().getPhysicalAddress().getAddressLine6());
        assertEquals("UK", result.getRecipientDetails().getPhysicalAddress().getAddressLine7());
        assertNotNull(result.getLetterDetails());
        assertEquals("template-456", result.getLetterDetails().getTemplateId());
        assertEquals("1.0", result.getLetterDetails().getTemplateVersion());
        assertEquals(TestUtil.DEFAULT_LETTER_CONTENT, result.getLetterDetails().getPersonalisationDetails());
        assertEquals(
                request.getCreatedAt().toInstant().truncatedTo(ChronoUnit.MILLIS),
                result.getCreatedAt().truncatedTo(ChronoUnit.MILLIS)
        );
    }

    @Test
    public void When_OffsetDateTimeToInstantMethodCalled_Expect_CorrectConversion() {
        OffsetDateTime dateTime = OffsetDateTime.parse("2023-01-01T12:00:00Z");

        Instant result = NotificationMapper.offsetDateTimeToInstant(dateTime);

        assertEquals(Instant.parse("2023-01-01T12:00:00Z"), result);
    }

    @Test
    public void When_OffsetDateTimeToInstantCalledWithNull_Expect_Null() {
        assertNull(NotificationMapper.offsetDateTimeToInstant(null));
    }
}
