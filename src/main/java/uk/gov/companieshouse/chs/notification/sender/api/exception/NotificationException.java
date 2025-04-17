package uk.gov.companieshouse.chs.notification.sender.api.exception;

public class NotificationException extends RuntimeException {

    public NotificationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
