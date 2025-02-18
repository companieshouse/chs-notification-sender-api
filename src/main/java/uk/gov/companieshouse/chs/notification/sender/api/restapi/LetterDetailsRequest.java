package uk.gov.companieshouse.chs.notification.sender.api.restapi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.annotation.Validated;
import uk.gov.companieshouse.api.chs_notification_sender.model.*;

import java.util.ArrayList;
@EqualsAndHashCode(callSuper = true)
@Data
@Validated
public class LetterDetailsRequest extends EmailLetterController{
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
}
