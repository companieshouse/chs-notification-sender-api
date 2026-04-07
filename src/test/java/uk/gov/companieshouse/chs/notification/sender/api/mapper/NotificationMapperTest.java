package uk.gov.companieshouse.chs.notification.sender.api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.companieshouse.api.chs.notification.sender.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.api.chs.notification.sender.model.GovUkLetterDetailsRequest;
import uk.gov.companieshouse.chs.notification.sender.api.TestUtil;
import uk.gov.companieshouse.notification.ChsEmailNotification;
import uk.gov.companieshouse.notification.ChsLetterNotification;

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
        assertEquals(request.getSenderDetails().getAppId(), result.getAppId());
        assertEquals(request.getSenderDetails().getReference(), result.getReference());
    }

    @Test
    void When_MapLetterRequest_Expect_CorrectChsLetterNotification() {
        GovUkLetterDetailsRequest request = TestUtil.createValidLetterRequest();

        ChsLetterNotification result = notificationMapper.mapToLetterDetailsRequest(request);

        assertNotNull(result);
        assertEquals(request.getSenderDetails().getAppId(), result.getAppId());
        assertEquals(request.getSenderDetails().getReference(), result.getReference());
    }

}
