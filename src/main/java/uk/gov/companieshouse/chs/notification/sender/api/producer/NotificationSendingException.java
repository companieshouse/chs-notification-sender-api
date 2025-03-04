package uk.gov.companieshouse.chs.notification.sender.api.producer;

public class NotificationSendingException extends RuntimeException {
    private static final long serialVersionUID = 206523116407987653L;

    public NotificationSendingException(String message, Throwable cause) {
        super(message, cause);
    }
}
