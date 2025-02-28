package uk.gov.companieshouse.chs.notification.sender.api.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import uk.gov.companieshouse.api.chs_notification_sender.model.EmailDetails;
import uk.gov.companieshouse.api.chs_notification_sender.model.RecipientDetailsEmail;
import uk.gov.companieshouse.api.chs_notification_sender.model.SenderDetails;

@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class EmailDetailsRequest {

    @NotNull
    @Size(min = 1, max = 1)
    @Valid
    private ArrayList<SenderDetails> senderDetails;

    @NotNull
    @Size(min = 1, max = 1)
    @Valid
    private ArrayList<RecipientDetailsEmail> recipientDetails;

    @NotNull
    @Size(min = 1, max = 1)
    @Valid
    private ArrayList<EmailDetails> emailDetails;

    @NotBlank()
    private String createdAt;

    public String convertToJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }
}