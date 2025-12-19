package uk.gov.companieshouse.chs.notification.sender.api.controller;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static helpers.utils.OutputAssertions.assertJsonHasAndEquals;
import static helpers.utils.OutputAssertions.getDataFromLogMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import helpers.OutputCapture;
import java.time.OffsetDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.companieshouse.api.chs.notification.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.api.chs.notification.model.GovUkLetterDetailsRequest;
import uk.gov.companieshouse.api.chs.notification.model.LetterDetails;
import uk.gov.companieshouse.api.chs.notification.model.RecipientDetailsLetter;
import uk.gov.companieshouse.api.chs.notification.model.SenderDetails;
import uk.gov.companieshouse.chs.notification.sender.api.TestUtil;
import uk.gov.companieshouse.chs.notification.sender.api.exception.NotificationException;
import uk.gov.companieshouse.chs.notification.sender.api.kafka.KafkaProducerService;
import uk.gov.companieshouse.logging.EventType;

@ExtendWith(MockitoExtension.class)
class NotificationSenderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private NotificationSenderController controller;

    private ObjectMapper objectMapper;

    private static Stream<Arguments> invalidEmailRequestProvider() {
        return Stream.of(
            Arguments.of(
                "Missing sender details",
                new GovUkEmailDetailsRequest()
                    .recipientDetails(TestUtil.createDefaultRecipientDetailsEmail())
                    .emailDetails(TestUtil.createDefaultEmailDetails())
                    .createdAt(OffsetDateTime.now()),
                new String[]{"senderDetails: must not be null"}
            ),
            Arguments.of(
                "Missing recipient details",
                new GovUkEmailDetailsRequest()
                    .senderDetails(TestUtil.createDefaultSenderDetails())
                    .emailDetails(TestUtil.createDefaultEmailDetails())
                    .createdAt(OffsetDateTime.now()),
                    new String[]{"recipientDetails: must not be null"}

            ),
            Arguments.of(
                "Missing email details",
                new GovUkEmailDetailsRequest()
                    .senderDetails(TestUtil.createDefaultSenderDetails())
                    .recipientDetails(TestUtil.createDefaultRecipientDetailsEmail())
                    .createdAt(OffsetDateTime.now()),
                new String[]{"emailDetails: must not be null"}
            ),
            Arguments.of(
                "Missing created at",
                new GovUkEmailDetailsRequest()
                    .senderDetails(TestUtil.createDefaultSenderDetails())
                    .recipientDetails(TestUtil.createDefaultRecipientDetailsEmail())
                    .emailDetails(TestUtil.createDefaultEmailDetails()),
                new String[]{"createdAt: must not be null"}
            ),
            Arguments.of(
                "Invalid email format",
                TestUtil.createEmailRequestWithCustomEmail("invalid-email-format"),
                new String[]{
                        "recipientDetails.emailAddress: must be a well-formed email address",
                        "recipientDetails.emailAddress: must match \"^[a-zA-Z0-9'._%+-]+@[a-zA-Z0-9'.-]+\\.[a-zA-Z]{2,}$\""
                }
            ),
            Arguments.of(
                "Empty email",
                TestUtil.createEmailRequestWithCustomEmail(""),
                new String[]{"recipientDetails.emailAddress: must match \"^[a-zA-Z0-9'._%+-]+@[a-zA-Z0-9'.-]+\\.[a-zA-Z]{2,}$\""}
            ),
            Arguments.of(
                "Null email",
                TestUtil.createEmailRequestWithCustomEmail(null),
                new String[]{"recipientDetails.emailAddress: must not be null"}
            ),
            Arguments.of(
                "Missing app ID in sender details",
                TestUtil.createValidEmailRequest()
                    .senderDetails(new SenderDetails(null, "test-reference")),
                new String[]{"senderDetails.appId: must not be null"}
            ),
            Arguments.of(
                "Missing reference in sender details",
                TestUtil.createValidEmailRequest()
                    .senderDetails(new SenderDetails("test-app-id", null)),
                new String[]{"senderDetails.reference: must not be null"}
            )
        );
    }

    private static Stream<Arguments> invalidLetterRequestProvider() {
        return Stream.of(
            Arguments.of(
                "Missing sender details",
                new GovUkLetterDetailsRequest()
                    .recipientDetails(TestUtil.createDefaultRecipientDetailsLetter())
                    .letterDetails(TestUtil.createDefaultLetterDetails())
                    .createdAt(OffsetDateTime.now()),
                    new String[]{"senderDetails: must not be null"}
            ),
            Arguments.of(
                "Missing recipient details",
                new GovUkLetterDetailsRequest()
                    .senderDetails(TestUtil.createDefaultSenderDetails())
                    .letterDetails(TestUtil.createDefaultLetterDetails())
                    .createdAt(OffsetDateTime.now()),
                    new String[]{"recipientDetails: must not be null"}
            ),
            Arguments.of(
                "Missing letter details",
                new GovUkLetterDetailsRequest()
                    .senderDetails(TestUtil.createDefaultSenderDetails())
                    .recipientDetails(TestUtil.createDefaultRecipientDetailsLetter())
                    .createdAt(OffsetDateTime.now()),
                new String[]{"letterDetails: must not be null"}
            ),
            Arguments.of(
                "Missing created at",
                new GovUkLetterDetailsRequest()
                    .senderDetails(TestUtil.createDefaultSenderDetails())
                    .recipientDetails(TestUtil.createDefaultRecipientDetailsLetter())
                    .letterDetails(TestUtil.createDefaultLetterDetails()),
                new String[]{"createdAt: must not be null"}
            ),
            Arguments.of(
                "Missing recipient name",
                TestUtil.createValidLetterRequest().recipientDetails(new RecipientDetailsLetter()
                    .physicalAddress(TestUtil.createDefaultAddress())),
                new String[]{"recipientDetails.name: must not be null"}
            ),
            Arguments.of(
                "Missing template ID in letter details",
                TestUtil.createValidLetterRequest().letterDetails(
                    new LetterDetails(null, TestUtil.DEFAULT_LETTER_CONTENT)),
                new String[]{"letterDetails.templateId: must not be null"}
            ),
            Arguments.of(
                "Missing personalisation details in letter details",
                TestUtil.createValidLetterRequest()
                    .letterDetails(new LetterDetails("template-456", null)),
                new String[]{"letterDetails.personalisationDetails: must not be null"}
            )
        );
    }

    private static Stream<Arguments> validLetterRequestProvider() {
        GovUkLetterDetailsRequest request = TestUtil.createValidLetterRequest();
        // Old letters did not include a letterId
        GovUkLetterDetailsRequest requestWitoutLetterId = TestUtil.createValidLetterRequest();
        requestWitoutLetterId.getLetterDetails().setLetterId(null);

        return Stream.of(
                Arguments.of(request),
                Arguments.of(requestWitoutLetterId)
        );
    }

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(controller)
            .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void When_ValidEmailRequest_Expect_CreatedStatus() throws Exception {
        GovUkEmailDetailsRequest request = TestUtil.createValidEmailRequest();
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/notification-sender/email")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Request-Id", "test-request-id")
                .content(requestJson))
            .andExpect(status().isCreated());

        verify(kafkaProducerService, times(1)).sendEmail(eq(request), anyString());
    }

    @ParameterizedTest
    @MethodSource("validLetterRequestProvider")
    void When_ValidLetterRequest_Expect_CreatedStatus(GovUkLetterDetailsRequest request) throws Exception {
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/notification-sender/letter")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Request-Id", "test-request-id")
                .content(requestJson))
            .andExpect(status().isCreated());

        verify(kafkaProducerService, times(1)).sendLetter(eq(request), anyString());
    }

    @Test
    void When_LetterRequestCausesException_Expect_InternalServerError() throws Exception {
        GovUkLetterDetailsRequest request = TestUtil.createValidLetterRequest();
        String requestJson = objectMapper.writeValueAsString(request);

        NotificationException exception = new NotificationException("Failed to send letter",
            new Throwable());
        doThrow(exception).when(kafkaProducerService).sendLetter(eq(request), anyString());

        mockMvc.perform(post("/notification-sender/letter")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Request-Id", "test-request-id")
                .content(requestJson))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.errors").value("Failed to process notification"))
            .andExpect(jsonPath("$.message").value("Failed to send letter"));
    }

    @ParameterizedTest
    @MethodSource("invalidEmailRequestProvider")
    void When_InvalidEmailRequest_Expect_BadRequest(String testName,
        GovUkEmailDetailsRequest request) throws Exception {
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/notification-sender/email")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Request-Id", "test-request-id")
                .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.errors").isArray());

        verify(kafkaProducerService, never()).sendEmail(any(), anyString());
    }

    @ParameterizedTest
    @MethodSource("invalidLetterRequestProvider")
    void When_InvalidLetterRequest_Expect_BadRequest(String testName,
        GovUkLetterDetailsRequest request) throws Exception {
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/notification-sender/letter")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Request-Id", "test-request-id")
                .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.errors").isArray());

        verify(kafkaProducerService, never()).sendLetter(any(), anyString());
    }


    @Test
    void When_LetterRequestWithoutRequestId_Expect_CreatedStatus() throws Exception {
        GovUkLetterDetailsRequest request = TestUtil.createValidLetterRequest();
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/notification-sender/letter")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Request-Id", "test-request-id")
                .content(requestJson))
            .andExpect(status().isCreated());

        verify(kafkaProducerService, times(1)).sendLetter(eq(request), anyString());
    }

    @Test
    void When_EmailRequestWithoutRequestId_Expect_CreatedStatus() throws Exception {
        GovUkEmailDetailsRequest request = TestUtil.createValidEmailRequest();
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/notification-sender/email")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Request-Id", "test-request-id")
                .content(requestJson))
            .andExpect(status().isCreated());

        verify(kafkaProducerService, times(1)).sendEmail(eq(request), anyString());
    }

    @Test
    void When_LetterRequestCausesException_Expect_ErrorLog() throws Exception {
        GovUkLetterDetailsRequest request = TestUtil.createValidLetterRequest();
        String requestJson = objectMapper.writeValueAsString(request);

        NotificationException exception = new NotificationException("Failed to send letter",
                new Throwable());
        doThrow(exception).when(kafkaProducerService).sendLetter(eq(request), anyString());

        try(var outputCapture = new OutputCapture()) {
            mockMvc.perform(post("/notification-sender/letter")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Request-Id", "test-request-id")
                    .content(requestJson));

            var logData = getDataFromLogMessage(outputCapture, EventType.ERROR,
                    "Failed to send notification");

            assertJsonHasAndEquals(logData, "status", "500");
            assertJsonHasAndEquals(logData, "errors", new String[]{"Failed to process notification"});
            assertJsonHasAndEquals(logData, "request_id", "test-request-id");
        }
    }

    @Test
    void When_ValidEmailRequest_Expect_Info_Logs() throws Exception {
        GovUkEmailDetailsRequest request = TestUtil.createValidEmailRequest();
        String requestJson = objectMapper.writeValueAsString(request);

        try (var outputCapture = new OutputCapture()) {
            mockMvc.perform(post("/notification-sender/email")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Request-Id", "test-request-id")
                            .content(requestJson))
                    .andExpect(status().isCreated());

            getDataFromLogMessage(outputCapture, EventType.INFO,
                    "Processing email notification request");
            getDataFromLogMessage(outputCapture, EventType.INFO,
                    "Email notification sent successfully");
        }
    }

    @ParameterizedTest
    @MethodSource("validLetterRequestProvider")
    void When_ValidLetterRequest_Expect_Info_Logs(GovUkLetterDetailsRequest request) throws Exception {
        String requestJson = objectMapper.writeValueAsString(request);

        try (var outputCapture = new OutputCapture()) {
            mockMvc.perform(post("/notification-sender/letter")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Request-Id", "test-request-id")
                            .content(requestJson))
                    .andExpect(status().isCreated());

            var logData = getDataFromLogMessage(outputCapture, EventType.INFO,
                    "Processing letter notification request");
            assertJsonHasAndEquals(logData, "request_id", "test-request-id");

            logData = getDataFromLogMessage(outputCapture, EventType.INFO,
                    "Letter notification sent successfully");
            assertJsonHasAndEquals(logData, "request_id", "test-request-id");
        }
    }

    @ParameterizedTest
    @MethodSource("invalidLetterRequestProvider")
    void When_InvalidLetterRequest_Expect_ErrorLogMessage(String testName,
            GovUkLetterDetailsRequest request, String[] expectedErrors) throws Exception {
        String requestJson = objectMapper.writeValueAsString(request);

        try(var outputCapture = new OutputCapture()) {
            mockMvc.perform(post("/notification-sender/letter")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Request-Id", "test-request-id")
                    .content(requestJson));

            var logData = getDataFromLogMessage(outputCapture, EventType.ERROR, "Validation error");
            assertJsonHasAndEquals(logData, "status", "400");
            assertJsonHasAndEquals(logData, "errors", expectedErrors);
            String stackTrace = logData.get("stack_trace").asText();
            assertThat(stackTrace, startsWith("org.springframework.web.bind.MethodArgumentNotValidException"));
        }

        verify(kafkaProducerService, never()).sendLetter(any(), anyString());
    }

    @ParameterizedTest
    @MethodSource("invalidEmailRequestProvider")
    void When_InvalidEmailRequest_Expect_ErrorLogMessage(String testName,
            GovUkEmailDetailsRequest request, String[] expectedErrors) throws Exception {
        String requestJson = objectMapper.writeValueAsString(request);

        try(var outputCapture = new OutputCapture()) {
            mockMvc.perform(post("/notification-sender/email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Request-Id", "test-request-id")
                    .content(requestJson));

            var logData = getDataFromLogMessage(outputCapture, EventType.ERROR, "Validation error");
            assertJsonHasAndEquals(logData, "status", "400");
            assertJsonHasAndEquals(logData, "errors", expectedErrors);

            String stackTrace = logData.get("stack_trace").asText();
            assertThat(stackTrace, startsWith("org.springframework.web.bind.MethodArgumentNotValidException"));
        }

        verify(kafkaProducerService, never()).sendLetter(any(), anyString());
    }
}
