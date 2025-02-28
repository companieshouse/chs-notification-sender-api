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
import uk.gov.companieshouse.api.chs_notification_sender.model.LetterDetails;
import uk.gov.companieshouse.api.chs_notification_sender.model.RecipientDetailsLetter;
import uk.gov.companieshouse.api.chs_notification_sender.model.SenderDetails;

import java.util.ArrayList;

@Data
@Validated
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class LetterDetailsRequest {
    @NotNull
    @Size(min = 1, max = 1)
    @Valid
    private ArrayList<SenderDetails> senderDetails;

    @NotNull
    @Size(min = 1, max = 1)
    @Valid
    private ArrayList<RecipientDetailsLetter> recipientDetails;

    @NotNull
    @Size(min = 1, max = 1)
    @Valid
    private ArrayList<LetterDetails> letterDetails;

    @NotBlank()
    private String createdAt;

    public String convertToJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }
}