package uk.gov.companieshouse.chs.notification.sender.api.restapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.chs_notification_sender.api.NotificationSenderInterface;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkLetterDetailsRequest;
import uk.gov.companieshouse.chs.notification.sender.api.utils.StaticPropertyUtil;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
@Validated
public class NotificationController implements NotificationSenderInterface {

    private static final Logger LOG = LoggerFactory.getLogger(StaticPropertyUtil.APPLICATION_NAMESPACE);

    private final NotificationService notificationService;

    public NotificationController(final NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * @param request
     * @param xRequestId Receive a request to send an email
     * @return senderDetails
     */
    @Override
    public ResponseEntity<Void> sendEmail(
        @RequestBody GovUkEmailDetailsRequest request,
        @RequestHeader(value = "X-Request-Id", required = false) String xRequestId) {

        if (request.getSenderDetails() == null ||
            request.getEmailDetails() == null ||
            request.getRecipientDetails() == null) {
            LOG.errorContext(xRequestId, new Exception("Bad request - Missing details"), null);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        LOG.infoContext(xRequestId, "Received request to send an email", null);

        byte[] serialisedMessage = notificationService.translateEmailNotification(request);
        //pass this onto kafka producer

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * @param request
     * @param xRequestId Receive a request to send a letter
     * @return
     */
    @Override
    public ResponseEntity<Void> sendLetter(
        @RequestBody GovUkLetterDetailsRequest request,
        @RequestHeader(value = "X-Request-Id", required = false) String xRequestId) {

        if (request.getSenderDetails() == null ||
            request.getLetterDetails() == null ||
            request.getRecipientDetails() == null) {
            LOG.errorContext(xRequestId, new Exception("Bad request - Missing details"), null);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        byte[] serialisedMessage = notificationService.translateLetterNotification(request);
        //pass this onto kafka producer

        LOG.infoContext(xRequestId, "Received request to send an letter", null);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}