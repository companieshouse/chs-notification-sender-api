package uk.gov.companieshouse.chs.notification.sender.api.restapi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.companieshouse.api.chs_notification_sender.model.*;

import java.math.BigDecimal;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;

@ExtendWith(MockitoExtension.class)
public class NotificationControllerTests {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    @Test
    public void validEmailRequest() {
        String xRequestId = "1";
        MockHttpServletRequest request1 = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request1));

        EmailDetails emailDetails = new EmailDetails();
        RecipientDetailsEmail recipientDetailsEmail = new RecipientDetailsEmail();
        SenderDetails senderDetails = new SenderDetails();

        GovUkEmailDetailsRequest govUkEmailDetailsRequest = new GovUkEmailDetailsRequest();
        govUkEmailDetailsRequest.setSenderDetails(senderDetails
                                                      .emailAddress("john.doe@email.address.net")
                                                      .userId("9876543")
                                                      .name("John Doe")
                                                      .reference("ref")
                                                      .appId("chips.send_email"));
        govUkEmailDetailsRequest.setEmailDetails(emailDetails
                                                     .templateId("template_id")
                                                     .templateVersion(BigDecimal.valueOf(1))
                                                     .personalisationDetails("letter_reference: 0123456789,company_name: BIG SHOP LTD,company_id: 9876543210,psc_type: 25% "));
        govUkEmailDetailsRequest.setRecipientDetails(recipientDetailsEmail
                                                         .emailAddress("john.doe@email.address.net")
                                                         .name("john doe"));


        ResponseEntity<Void> response = notificationController.sendEmail(govUkEmailDetailsRequest, xRequestId);

        assertThat(response.getStatusCode()).isEqualTo(CREATED);
        Assertions.assertNotNull(response);
    }

    @Test
    public void invalidEmailRequest() {
        String xRequestId = "1";
        MockHttpServletRequest request1 = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request1));

        SenderDetails senderDetails = new SenderDetails();

        GovUkEmailDetailsRequest govUkEmailDetailsRequest = new GovUkEmailDetailsRequest();
        govUkEmailDetailsRequest.setSenderDetails(senderDetails
                                                      .emailAddress("john.doe@email.address.net")
                                                      .userId("9876543")
                                                      .name("John Doe")
                                                      .reference("ref")
                                                      .appId("chips.send_email"));


        ResponseEntity<Void> response = notificationController.sendEmail(govUkEmailDetailsRequest, xRequestId);


        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void invalidEmailRequestNull() {
        String xRequestId = "1";
        MockHttpServletRequest request1 = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request1));

        SenderDetails senderDetails = new SenderDetails();

        GovUkEmailDetailsRequest govUkEmailDetailsRequest = new GovUkEmailDetailsRequest();
        govUkEmailDetailsRequest.setSenderDetails(senderDetails
                                                      .emailAddress("john.doe@email.address.net")
                                                      .userId("9876543")
                                                      .name("John Doe")
                                                      .reference("ref")
                                                      .appId("chips.send_email"));
        govUkEmailDetailsRequest.setEmailDetails(null);
        govUkEmailDetailsRequest.setRecipientDetails(null);

        ResponseEntity<Void> response = notificationController.sendEmail(govUkEmailDetailsRequest, xRequestId);


        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void validLetterRequest() {
        String xRequestId = "1";
        MockHttpServletRequest request1 = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request1));

        LetterDetails letterDetails = new LetterDetails();
        RecipientDetailsLetter recipientDetailsLetter = new RecipientDetailsLetter();
        SenderDetails senderDetails = new SenderDetails();
        Address address = new Address();

        GovUkLetterDetailsRequest govUkletterDetailsRequest = new GovUkLetterDetailsRequest();
        govUkletterDetailsRequest.setLetterDetails(letterDetails
                                                       .templateId("template_id")
                                                       .templateVersion(BigDecimal.valueOf(1))
                                                       .personalisationDetails("letter_reference: 0123456789,company_name: BIG SHOP LTD,company_id: 9876543210,psc_type: 25% "));
        govUkletterDetailsRequest.setRecipientDetails(recipientDetailsLetter
                                                          .name("john doe")
                                                          .physicalAddress(Collections.singletonList(
                                                              address
                                                                  .addressLine1("address_line_1")
                                                                  .addressLine2("address_line_2")
                                                                  .addressLine3("address_line_3"))));
        govUkletterDetailsRequest.setSenderDetails(senderDetails
                                                       .appId("chips.send_letter")
                                                       .reference("ref")
                                                       .name("John Doe")
                                                       .userId("9876543")
                                                       .emailAddress("john.doe@email.address.net"));

        ResponseEntity<Void> response = notificationController.sendLetter(govUkletterDetailsRequest, xRequestId);

        assertThat(response.getStatusCode()).isEqualTo(CREATED);
        Assertions.assertNotNull(response);
    }

    @Test
    void invalidLetterRequest() {
        String xRequestId = "1";
        MockHttpServletRequest request1 = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request1));

        LetterDetails letterDetails = new LetterDetails();
        GovUkLetterDetailsRequest govUkLetterDetailsRequest = new GovUkLetterDetailsRequest();

        govUkLetterDetailsRequest.setLetterDetails(letterDetails.personalisationDetails(""));

        ResponseEntity<Void> response = notificationController.sendLetter(govUkLetterDetailsRequest, xRequestId);

        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void invalidLetterRequestNull() {
        String xRequestId = "1";
        MockHttpServletRequest request1 = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request1));

        SenderDetails senderDetails = new SenderDetails();

        GovUkLetterDetailsRequest govUkLetterDetailsRequest = new GovUkLetterDetailsRequest();
        govUkLetterDetailsRequest.setSenderDetails(senderDetails
                                                       .emailAddress("john.doe@email.address.net")
                                                       .userId("9876543")
                                                       .name("John Doe")
                                                       .reference("ref")
                                                       .appId("chips.send_email"));
        govUkLetterDetailsRequest.setLetterDetails(null);
        govUkLetterDetailsRequest.setRecipientDetails(null);

        ResponseEntity<Void> response = notificationController.sendLetter(govUkLetterDetailsRequest, xRequestId);


        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }
}
