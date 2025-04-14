package uk.gov.companieshouse.chs.notification.sender.api.controller;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class NotificationSenderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private NotificationSenderController controller;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(controller)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    public void When_ValidEmailRequest_Expect_CreatedStatus() throws Exception {
        GovUkEmailDetailsRequest request = TestUtil.createValidEmailRequest();
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/notification-sender/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Request-Id", "test-request-id")
                        .content(requestJson))
                .andExpect(status().isCreated());

        verify(kafkaProducerService, times(1)).sendEmail(any(GovUkEmailDetailsRequest.class));
    }

    @Test
    public void When_ValidLetterRequest_Expect_CreatedStatus() throws Exception {
        GovUkLetterDetailsRequest request = TestUtil.createValidLetterRequest();
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/notification-sender/letter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Request-Id", "test-request-id")
                        .content(requestJson))
                .andExpect(status().isCreated());

        verify(kafkaProducerService, times(1)).sendLetter(any(GovUkLetterDetailsRequest.class));
    }

    @Test
    public void When_LetterRequestCausesException_Expect_InternalServerError() throws Exception {
        GovUkLetterDetailsRequest request = TestUtil.createValidLetterRequest();
        String requestJson = objectMapper.writeValueAsString(request);

        NotificationException exception = new NotificationException("Failed to send letter", new Throwable());
        doThrow(exception).when(kafkaProducerService).sendLetter(any(GovUkLetterDetailsRequest.class));

        mockMvc.perform(post("/notification-sender/letter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Request-Id", "test-request-id")
                        .content(requestJson))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Failed to process notification"))
                .andExpect(jsonPath("$.message").value("Failed to send letter"));
    }

    @ParameterizedTest
    @MethodSource("invalidEmailRequestProvider")
    public void When_InvalidEmailRequest_Expect_BadRequest(String testName, GovUkEmailDetailsRequest request) throws Exception {
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/notification-sender/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Request-Id", "test-request-id")
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray());

        verify(kafkaProducerService, never()).sendEmail(any());
    }

    @ParameterizedTest
    @MethodSource("invalidLetterRequestProvider")
    public void When_InvalidLetterRequest_Expect_BadRequest(String testName, GovUkLetterDetailsRequest request) throws Exception {
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/notification-sender/letter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Request-Id", "test-request-id")
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray());

        verify(kafkaProducerService, never()).sendLetter(any());
    }

    @Test
    public void When_LetterRequestWithoutRequestId_Expect_CreatedStatus() throws Exception {
        GovUkLetterDetailsRequest request = TestUtil.createValidLetterRequest();
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/notification-sender/letter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated());

        verify(kafkaProducerService, times(1)).sendLetter(any(GovUkLetterDetailsRequest.class));
    }

    @Test
    public void When_EmailRequestWithoutRequestId_Expect_CreatedStatus() throws Exception {
        GovUkEmailDetailsRequest request = TestUtil.createValidEmailRequest();
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/notification-sender/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated());

        verify(kafkaProducerService, times(1)).sendEmail(any(GovUkEmailDetailsRequest.class));
    }

    private static Stream<Arguments> invalidEmailRequestProvider() {
        return Stream.of(
                Arguments.of(
                        "Missing sender details",
                        new GovUkEmailDetailsRequest()
                                .recipientDetails(TestUtil.createDefaultRecipientDetailsEmail())
                                .emailDetails(TestUtil.createDefaultEmailDetails())
                                .createdAt(OffsetDateTime.now())
                ),
                Arguments.of(
                        "Missing recipient details",
                        new GovUkEmailDetailsRequest()
                                .senderDetails(TestUtil.createDefaultSenderDetails())
                                .emailDetails(TestUtil.createDefaultEmailDetails())
                                .createdAt(OffsetDateTime.now())
                ),
                Arguments.of(
                        "Missing email details",
                        new GovUkEmailDetailsRequest()
                                .senderDetails(TestUtil.createDefaultSenderDetails())
                                .recipientDetails(TestUtil.createDefaultRecipientDetailsEmail())
                                .createdAt(OffsetDateTime.now())
                ),
                Arguments.of(
                        "Missing created at",
                        new GovUkEmailDetailsRequest()
                                .senderDetails(TestUtil.createDefaultSenderDetails())
                                .recipientDetails(TestUtil.createDefaultRecipientDetailsEmail())
                                .emailDetails(TestUtil.createDefaultEmailDetails())
                ),
                Arguments.of(
                        "Invalid email format",
                        TestUtil.createEmailRequestWithCustomEmail("invalid-email-format")
                ),
                Arguments.of(
                        "Empty email",
                        TestUtil.createEmailRequestWithCustomEmail("")
                ),
                Arguments.of(
                        "Null email",
                        TestUtil.createEmailRequestWithCustomEmail(null)
                ),
                Arguments.of(
                        "Missing app ID in sender details",
                        TestUtil.createValidEmailRequest().senderDetails(new SenderDetails(null, "test-reference"))
                ),
                Arguments.of(
                        "Missing reference in sender details",
                        TestUtil.createValidEmailRequest().senderDetails(new SenderDetails("test-app-id", null))
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
                                .createdAt(OffsetDateTime.now())
                ),
                Arguments.of(
                        "Missing recipient details",
                        new GovUkLetterDetailsRequest()
                                .senderDetails(TestUtil.createDefaultSenderDetails())
                                .letterDetails(TestUtil.createDefaultLetterDetails())
                                .createdAt(OffsetDateTime.now())
                ),
                Arguments.of(
                        "Missing letter details",
                        new GovUkLetterDetailsRequest()
                                .senderDetails(TestUtil.createDefaultSenderDetails())
                                .recipientDetails(TestUtil.createDefaultRecipientDetailsLetter())
                                .createdAt(OffsetDateTime.now())
                ),
                Arguments.of(
                        "Missing created at",
                        new GovUkLetterDetailsRequest()
                                .senderDetails(TestUtil.createDefaultSenderDetails())
                                .recipientDetails(TestUtil.createDefaultRecipientDetailsLetter())
                                .letterDetails(TestUtil.createDefaultLetterDetails())
                ),
                Arguments.of(
                        "Missing recipient name",
                        TestUtil.createValidLetterRequest().recipientDetails(new RecipientDetailsLetter()
                                .physicalAddress(TestUtil.createDefaultAddress()))
                ),
                Arguments.of(
                        "Missing template ID in letter details",
                        TestUtil.createValidLetterRequest().letterDetails(new LetterDetails(null, new BigDecimal("1.0"), TestUtil.DEFAULT_LETTER_CONTENT))
                ),
                Arguments.of(
                        "Missing template version in letter details",
                        TestUtil.createValidLetterRequest().letterDetails(new LetterDetails("template-456", null, TestUtil.DEFAULT_LETTER_CONTENT))
                ),
                Arguments.of(
                        "Missing personalisation details in letter details",
                        TestUtil.createValidLetterRequest().letterDetails(new LetterDetails("template-456", new BigDecimal("1.0"), null))
                )
        );
    }


}
