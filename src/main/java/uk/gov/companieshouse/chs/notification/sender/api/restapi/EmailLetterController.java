package uk.gov.companieshouse.chs.notification.sender.api.restapi;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.chs_notification_sender.api.NotificationSenderInterface;
import uk.gov.companieshouse.api.chs_notification_sender.model.*;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
public class EmailLetterController implements NotificationSenderInterface {

    private static final Logger LOG = LoggerFactory.getLogger(StaticPropertyUtil.APPLICATION_NAMESPACE);

    /**
     * @param request
     * @param xRequestId Receive a request to send a email
     * @return senderDetails
     */
    @Override
    public ResponseEntity<Void> sendEmail(final GovUkEmailDetailsRequest request, final String xRequestId) {
        LOG.infoContext(xRequestId, "Received request to send an email", null);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * @param request
     * @param xRequestId Receive a request to send a letter
     * @return
     */
    @Override
    public ResponseEntity<Void> sendLetter(final GovUkLetterDetailsRequest request, final String xRequestId) {
        LOG.infoContext(xRequestId, "Received request to send an letter", null);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}