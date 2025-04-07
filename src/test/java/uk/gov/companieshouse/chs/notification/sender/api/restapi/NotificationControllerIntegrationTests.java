package uk.gov.companieshouse.chs.notification.sender.api.restapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import uk.gov.companieshouse.api.chs_notification_sender.model.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class NotificationControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private NotificationService notificationService;

    @Autowired
    private NotificationController notificationController;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(notificationController, "notificationService", notificationService);
    }

    private static final String EMAIL_ENDPOINT = "/notification-sender/email";
    private static final String LETTER_ENDPOINT = "/notification-sender/letter";
    private static final String X_REQUEST_ID = "X-Request-ID";

    // ERIC Authentication Headers
    private static final String ERIC_IDENTITY = "ERIC-Identity";
    private static final String ERIC_IDENTITY_TYPE = "ERIC-Identity-Type";
    private static final String ERIC_AUTHORISED_KEY_ROLES = "ERIC-Authorised-Key-Roles";

    // ERIC Authentication Values
    private static final String VALID_ERIC_IDENTITY = "67ZeMsvAEgkBWs7tNKacdrPvOmQ";
    private static final String VALID_ERIC_IDENTITY_TYPE = "key";
    private static final String VALID_ERIC_AUTHORISED_KEY_ROLES = "*";

    @Test
    public void When_SendingEmailWithValidCredentials_Expect_CreatedStatus() throws Exception {
        when(notificationService.translateEmailNotification(any())).thenReturn(new byte[]{1, 2, 3});
        doNothing().when(notificationService).sendEmail(any());

        GovUkEmailDetailsRequest request = createEmailDetailsRequest();

        mockMvc.perform(post(EMAIL_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_REQUEST_ID, "test-request-id")
                        .header(ERIC_IDENTITY, VALID_ERIC_IDENTITY)
                        .header(ERIC_IDENTITY_TYPE, VALID_ERIC_IDENTITY_TYPE)
                        .header(ERIC_AUTHORISED_KEY_ROLES, VALID_ERIC_AUTHORISED_KEY_ROLES)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    public void When_SendingLetterWithValidCredentials_Expect_CreatedStatus() throws Exception {
        when(notificationService.translateLetterNotification(any())).thenReturn(new byte[]{1, 2, 3});
        doNothing().when(notificationService).sendLetter(any());

        GovUkLetterDetailsRequest request = createLetterDetailsRequest();

        mockMvc.perform(post(LETTER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_REQUEST_ID, "test-request-id")
                        .header(ERIC_IDENTITY, VALID_ERIC_IDENTITY)
                        .header(ERIC_IDENTITY_TYPE, VALID_ERIC_IDENTITY_TYPE)
                        .header(ERIC_AUTHORISED_KEY_ROLES, VALID_ERIC_AUTHORISED_KEY_ROLES)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @MethodSource("provideHeadersTestCases")
    public void When_SendingEmailWithInvalidOrMissingHeaders_Expect_ForbiddenStatus(String missingHeader, String headerValue, ResultMatcher response) throws Exception {
        GovUkEmailDetailsRequest request = createEmailDetailsRequest();

        MockHttpServletRequestBuilder requestBuilder = post(EMAIL_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .header(X_REQUEST_ID, "test-request-id")
                .content(objectMapper.writeValueAsString(request));

        // Add all headers except the one we want to test as missing
        if (!ERIC_IDENTITY.equals(missingHeader)) {
            requestBuilder.header(ERIC_IDENTITY, headerValue != null ? headerValue : VALID_ERIC_IDENTITY);
        }
        if (!ERIC_IDENTITY_TYPE.equals(missingHeader)) {
            requestBuilder.header(ERIC_IDENTITY_TYPE, headerValue != null ? headerValue : VALID_ERIC_IDENTITY_TYPE);
        }
        if (!ERIC_AUTHORISED_KEY_ROLES.equals(missingHeader)) {
            requestBuilder.header(ERIC_AUTHORISED_KEY_ROLES, headerValue != null ? headerValue : VALID_ERIC_AUTHORISED_KEY_ROLES);
        }

        mockMvc.perform(requestBuilder)
                .andExpect(response);
    }

    @ParameterizedTest
    @MethodSource("provideHeadersTestCases")
    public void When_SendingLetterWithInvalidOrMissingHeaders_Expect_ForbiddenStatus(String missingHeader, String headerValue, ResultMatcher response) throws Exception {
        GovUkLetterDetailsRequest request = createLetterDetailsRequest();

        MockHttpServletRequestBuilder requestBuilder = post(LETTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .header(X_REQUEST_ID, "test-request-id")
                .content(objectMapper.writeValueAsString(request));

        // Add all headers except the one we want to test as missing
        if (!ERIC_IDENTITY.equals(missingHeader)) {
            requestBuilder.header(ERIC_IDENTITY, headerValue != null ? headerValue : VALID_ERIC_IDENTITY);
        }
        if (!ERIC_IDENTITY_TYPE.equals(missingHeader)) {
            requestBuilder.header(ERIC_IDENTITY_TYPE, headerValue != null ? headerValue : VALID_ERIC_IDENTITY_TYPE);
        }
        if (!ERIC_AUTHORISED_KEY_ROLES.equals(missingHeader)) {
            requestBuilder.header(ERIC_AUTHORISED_KEY_ROLES, headerValue != null ? headerValue : VALID_ERIC_AUTHORISED_KEY_ROLES);
        }

        mockMvc.perform(requestBuilder)
                .andExpect(response);
    }

    private static Stream<Arguments> provideHeadersTestCases() {
        return Stream.of(
                // Test missing headers
                Arguments.of(ERIC_IDENTITY, null, status().isUnauthorized()),
                Arguments.of(ERIC_IDENTITY_TYPE, null, status().isForbidden()),
                Arguments.of(ERIC_AUTHORISED_KEY_ROLES, null, status().isForbidden()),

                // Test invalid header values
                Arguments.of(ERIC_IDENTITY, "invalid-identity", status().isUnauthorized()),
                Arguments.of(ERIC_IDENTITY_TYPE, "invalid-type", status().isForbidden()),
                Arguments.of(ERIC_AUTHORISED_KEY_ROLES, "invalid-role", status().isForbidden())
        );
    }

    private GovUkEmailDetailsRequest createEmailDetailsRequest() {
        GovUkEmailDetailsRequest request = new GovUkEmailDetailsRequest();
        EmailDetails emailDetails = new EmailDetails()
                .templateId("template_id")
                .templateVersion(BigDecimal.valueOf(1))
                .personalisationDetails("letter_reference: 0123456789,company_name: BIG SHOP LTD");
        SenderDetails senderDetails = new SenderDetails()
                .emailAddress("john.doe@email.address.net")
                .userId("9876543")
                .name("John Doe")
                .reference("ref")
                .appId("chips.send_email");
        RecipientDetailsEmail recipientDetails = new RecipientDetailsEmail()
                .emailAddress("john.doe@email.address.net")
                .name("john doe");

        request.setEmailDetails(emailDetails);
        request.setSenderDetails(senderDetails);
        request.setRecipientDetails(recipientDetails);
        request.setCreatedAt(OffsetDateTime.now());
        return request;
    }

    private GovUkLetterDetailsRequest createLetterDetailsRequest() {
        GovUkLetterDetailsRequest request = new GovUkLetterDetailsRequest();
        LetterDetails letterDetails = new LetterDetails()
                .templateId("template_id")
                .templateVersion(BigDecimal.valueOf(1))
                .personalisationDetails("letter_reference: 0123456789,company_name: BIG SHOP LTD");
        SenderDetails senderDetails = new SenderDetails()
                .emailAddress("john.doe@email.address.net")
                .userId("9876543")
                .name("John Doe")
                .reference("ref")
                .appId("chips.send_letter");
        Address address = new Address()
                .addressLine1("address_line_1")
                .addressLine2("address_line_2")
                .addressLine3("address_line_3")
                .addressLine4("address_line_4")
                .addressLine5("address_line_5")
                .addressLine6("address_line_6")
                .addressLine7("address_line_7");

        RecipientDetailsLetter recipientDetails = new RecipientDetailsLetter()
                .name("john doe")
                .physicalAddress(address);

        request.setLetterDetails(letterDetails);
        request.setSenderDetails(senderDetails);
        request.setRecipientDetails(recipientDetails);
        request.setCreatedAt(OffsetDateTime.now());
        return request;
    }
}
