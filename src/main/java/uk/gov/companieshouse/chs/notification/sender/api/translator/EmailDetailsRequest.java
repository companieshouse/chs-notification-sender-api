package uk.gov.companieshouse.chs.notification.sender.api.translator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import uk.gov.companieshouse.api.chs_notification_sender.model.EmailDetails;
import uk.gov.companieshouse.api.chs_notification_sender.model.RecipientDetailsEmail;
import uk.gov.companieshouse.api.chs_notification_sender.model.SenderDetails;

import java.util.ArrayList;

@Data
@Validated
@NoArgsConstructor(force = true)
@AllArgsConstructor
class EmailDetailsRequest {

    @NotNull
    @Size(min = 1, max = 1)
    @Valid
    private final ArrayList<SenderDetails> senderDetails;

    @NotNull
    @Size(min = 1, max = 1)
    @Valid
    private final ArrayList<RecipientDetailsEmail> recipientDetails;

    @NotNull
    @Size(min = 1, max = 1)
    @Valid
    private final ArrayList<EmailDetails> emailDetails;

    @NotBlank()
    private final String createdAt;

    public String convertToJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }
}