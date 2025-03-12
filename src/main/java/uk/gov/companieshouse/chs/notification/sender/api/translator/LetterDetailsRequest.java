package uk.gov.companieshouse.chs.notification.sender.api.translator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import uk.gov.companieshouse.api.chs_notification_sender.model.LetterDetails;
import uk.gov.companieshouse.api.chs_notification_sender.model.RecipientDetailsLetter;
import uk.gov.companieshouse.api.chs_notification_sender.model.SenderDetails;

import java.time.OffsetDateTime;

@Validated
record LetterDetailsRequest(
    @NotNull
    @Size(min = 1, max = 1)
    @Valid
    SenderDetails senderDetails,

    @NotNull
    @Size(min = 1, max = 1)
    @Valid
    RecipientDetailsLetter recipientDetails,

    @NotNull
    @Size(min = 1, max = 1)
    @Valid
    LetterDetails letterDetails,

    @NotBlank()
    OffsetDateTime createdAt
) {
    public String convertToJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsString(this);
    }
}