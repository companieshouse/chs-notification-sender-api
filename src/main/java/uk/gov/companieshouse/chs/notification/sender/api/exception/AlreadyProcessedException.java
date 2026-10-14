package uk.gov.companieshouse.chs.notification.sender.api.exception;

import static java.lang.String.format;

import uk.gov.companieshouse.api.chs.notification.sender.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.api.chs.notification.sender.model.GovUkLetterDetailsRequest;

public class AlreadyProcessedException extends RuntimeException {

    public AlreadyProcessedException(GovUkEmailDetailsRequest govUkEmailDetailsRequest) {
        super(format("Duplicate email request found for %s%s",
                govUkEmailDetailsRequest.getSenderDetails().getAppId(),
                govUkEmailDetailsRequest.getSenderDetails().getReference()));
    }

    public AlreadyProcessedException(GovUkLetterDetailsRequest govUkLetterDetailsRequest) {
        super(format("Duplicate letter request found for %s%s",
                govUkLetterDetailsRequest.getSenderDetails().getAppId(),
                govUkLetterDetailsRequest.getSenderDetails().getReference()));
    }
}
