package uk.gov.companieshouse.chs.notification.sender.api.utils;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.companieshouse.api.chs_notification_sender.model.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Objects;

public class NotificationTestDataManager {
    private static NotificationTestDataManager instance;

    public static NotificationTestDataManager getInstance(){
        if ( Objects.isNull( instance ) ){
            instance = new NotificationTestDataManager();
        }
        return instance;
    }
    
    private NotificationTestDataManager() {
        instantiateEmailAndLetterRequests();
    }

    private void instantiateEmailAndLetterRequests() {

    }

    public GovUkEmailDetailsRequest generateGovUkEmailDetailsRequest() {
        EmailDetails emailDetails = new EmailDetails();
        RecipientDetailsEmail recipientDetailsEmail = new RecipientDetailsEmail();
        SenderDetails senderDetails = new SenderDetails();

        GovUkEmailDetailsRequest govUkEmailDetailsRequest = new GovUkEmailDetailsRequest();
        govUkEmailDetailsRequest.addSenderDetailsItem(senderDetails.emailAddress("john.doe@email.address.net")
                .userId("9876543").name("John Doe").reference("ref").appId("chips.send_email"));
        govUkEmailDetailsRequest.addEmailDetailsItem(emailDetails.templateId("template_id")
                .templateVersion(BigDecimal.valueOf(1))
                .personalisationDetails("letter_reference: 0123456789,company_name: BIG SHOP LTD,company_id: 9876543210,psc_type: 25% "));
        govUkEmailDetailsRequest.addRecipientDetailsItem(recipientDetailsEmail
                .emailAddress("john.doe@email.address.net").name("john doe"));

        return govUkEmailDetailsRequest;
    }

    public GovUkLetterDetailsRequest generateGovUkLetterDetailsRequest() {
            String xRequestId = "1";
            MockHttpServletRequest request1 = new MockHttpServletRequest();
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request1));

            LetterDetails letterDetails = new LetterDetails();
            RecipientDetailsLetter recipientDetailsLetter = new RecipientDetailsLetter();
            SenderDetails senderDetails = new SenderDetails();
            Address address = new Address();

            GovUkLetterDetailsRequest govUkletterDetailsRequest = new GovUkLetterDetailsRequest();
            govUkletterDetailsRequest.addLetterDetailsItem(letterDetails.templateId("template_id")
                    .templateVersion(BigDecimal.valueOf(1))
                    .personalisationDetails("letter_reference: 0123456789,company_name: BIG SHOP LTD,company_id: 9876543210,psc_type: 25% "));
            govUkletterDetailsRequest.addRecipientDetailsItem(recipientDetailsLetter.name("john doe")
                    .physicalAddress(Collections.singletonList(
                            address.addressLine1("address_line_1")
                                    .addressLine2("address_line_2")
                                    .addressLine3("address_line_3"))));
            govUkletterDetailsRequest.addSenderDetailsItem(senderDetails.appId("chips.send_letter")
                    .reference("ref")
                    .name("John Doe")
                    .userId("9876543")
                    .emailAddress("john.doe@email.address.net"));

            return govUkletterDetailsRequest;
    }


}
