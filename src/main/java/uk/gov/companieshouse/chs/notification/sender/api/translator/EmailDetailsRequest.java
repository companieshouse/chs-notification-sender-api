package uk.gov.companieshouse.chs.notification.sender.api.translator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import uk.gov.companieshouse.api.chs_notification_sender.model.EmailDetails;
import uk.gov.companieshouse.api.chs_notification_sender.model.RecipientDetailsEmail;
import uk.gov.companieshouse.api.chs_notification_sender.model.SenderDetails;

import java.util.ArrayList;
import java.util.List;

@Validated
class EmailDetailsRequest {
    public EmailDetailsRequest() {
        senderDetails = new ArrayList<SenderDetails>();
        recipientDetails = new ArrayList<RecipientDetailsEmail>();
        emailDetails = new ArrayList<EmailDetails>();
        createdAt = "";
    }

    public EmailDetailsRequest(ArrayList<SenderDetails> senderDetails, ArrayList<RecipientDetailsEmail> recipientDetails, ArrayList<EmailDetails> emailDetails, String createdAt) {
        this.senderDetails = senderDetails;
        this.recipientDetails = recipientDetails;
        this.emailDetails = emailDetails;
        this.createdAt = createdAt;
    }

    @NotNull
    @Size(min = 1, max = 1)
    @Valid
    private final List<SenderDetails> senderDetails;

    @NotNull
    @Size(min = 1, max = 1)
    @Valid
    private final List<RecipientDetailsEmail> recipientDetails;

    @NotNull
    @Size(min = 1, max = 1)
    @Valid
    private final List<EmailDetails> emailDetails;

    @NotBlank()
    private final String createdAt;

    public String convertToJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }
}