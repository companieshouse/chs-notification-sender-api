package uk.gov.companieshouse.chs.notification.sender.api.producer;

import java.io.Serial;

public class NotificationSendingException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 206523116407987653L;

    public NotificationSendingException(String message, Throwable cause) {
        super(message, cause);
    }
}
