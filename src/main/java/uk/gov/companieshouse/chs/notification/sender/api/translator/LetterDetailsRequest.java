package uk.gov.companieshouse.chs.notification.sender.api.translator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import uk.gov.companieshouse.api.chs_notification_sender.model.LetterDetails;
import uk.gov.companieshouse.api.chs_notification_sender.model.RecipientDetailsLetter;
import uk.gov.companieshouse.api.chs_notification_sender.model.SenderDetails;

import java.util.ArrayList;
import java.util.List;

@Validated
class LetterDetailsRequest {
    public LetterDetailsRequest() {
        this.createdAt = createdAt;
        this.letterDetails = new ArrayList<>();
        this.recipientDetails = new ArrayList<>();
        this.senderDetails = new ArrayList<>();
    }

    public LetterDetailsRequest(String createdAt, List<LetterDetails> letterDetails, List<RecipientDetailsLetter> recipientDetails, List<SenderDetails> senderDetails) {
        this.createdAt = createdAt;
        this.letterDetails = letterDetails;
        this.recipientDetails = recipientDetails;
        this.senderDetails = senderDetails;
    }

    @NotNull
    @Size(min = 1, max = 1)
    @Valid
    private final List<SenderDetails> senderDetails;

    @NotNull
    @Size(min = 1, max = 1)
    @Valid
    private final List<RecipientDetailsLetter> recipientDetails;

    @NotNull
    @Size(min = 1, max = 1)
    @Valid
    private final List<LetterDetails> letterDetails;

    @NotBlank()
    private String createdAt;

    public String convertToJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }
}