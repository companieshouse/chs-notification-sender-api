package uk.gov.companieshouse.chs.notification.sender.api.restapi;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.chs_notification_sender.api.NotificationSenderInterface;
import uk.gov.companieshouse.api.chs_notification_sender.model.*;
import uk.gov.companieshouse.chs.notification.sender.api.utils.StaticPropertyUtil;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.concurrent.ExecutionException;

@RestController
public class NotificationController implements NotificationSenderInterface {

    private static final Logger LOG = LoggerFactory.getLogger(StaticPropertyUtil.APPLICATION_NAMESPACE);

    private final NotificationService notificationService;

    public NotificationController(final NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * @param request The request itself
     * @param xRequestId Receive a request to send an email
     * @return senderDetails
     */
    @Override
    public ResponseEntity<Void> sendEmail(final GovUkEmailDetailsRequest request, final String xRequestId) {

        if (request.getEmailDetails().isEmpty() || request.getSenderDetails().isEmpty() || request.getRecipientDetails().isEmpty()
                || (request.getEmailDetails().contains(null) || request.getSenderDetails().contains(null) || request.getRecipientDetails().contains(null))) {
            LOG.errorContext(xRequestId, new Exception("Bad request - Missing details"), null);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            LOG.infoContext(xRequestId, "Received request to send an email", null);
            String emailTopic = "";
            byte[] serialisedMessage = notificationService.translateEmailNotification(request);
            try {
                notificationService.sendEmail(emailTopic,serialisedMessage);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }

    /**
     * @param request
     * @param xRequestId Receive a request to send a letter
     * @return
     */
    @Override
    public ResponseEntity<Void> sendLetter(final GovUkLetterDetailsRequest request, final String xRequestId) {
        if (request.getSenderDetails().isEmpty() || request.getLetterDetails().isEmpty() || request.getRecipientDetails().isEmpty()
                || (request.getLetterDetails().contains(null) || request.getSenderDetails().contains(null) || request.getRecipientDetails().contains(null))) {
            LOG.errorContext(xRequestId, new Exception("Bad request - Missing details"), null);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            LOG.infoContext(xRequestId, "Received request to send a letter", null);
            byte[] serialisedMessage = notificationService.translateLetterNotification(request);
            String letterTopic = "";
            try {
                notificationService.sendLetter(letterTopic, serialisedMessage);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }
}