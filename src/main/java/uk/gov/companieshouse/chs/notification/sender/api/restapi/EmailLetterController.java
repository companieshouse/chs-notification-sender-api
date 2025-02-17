package uk.gov.companieshouse.chs.notification.sender.api.restapi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.chs_notification_sender.api.NotificationSenderInterface;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkLetterDetailsRequest;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
public class EmailLetterController implements NotificationSenderInterface {
//    private final service;
//    private final EmailService emailService;
//    private final LetterService letterService;

    private static final Logger LOG = LoggerFactory.getLogger( StaticPropertyUtil.APPLICATION_NAMESPACE );

    public EmailLetterController(){

    }

    /**
     * @param govUkEmailDetailsRequest
     * @param s
     * @return
     */
    @Override
    public ResponseEntity<Void> sendEmail(@Valid GovUkEmailDetailsRequest govUkEmailDetailsRequest, @Pattern(regexp = "[0-9A-Za-z-_]{8,32}") String s) {
        return null;
    }

    /**
     * @param govUkLetterDetailsRequest
     * @param s
     * @return
     */
    @Override
    public ResponseEntity<Void> sendLetter(@Valid GovUkLetterDetailsRequest govUkLetterDetailsRequest, @Pattern(regexp = "[0-9A-Za-z-_]{8,32}") String s) {
        return null;
    }
}
