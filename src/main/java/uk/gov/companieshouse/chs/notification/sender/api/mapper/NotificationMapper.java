package uk.gov.companieshouse.chs.notification.sender.api.mapper;

import java.time.Instant;
import java.time.OffsetDateTime;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.chs.notification.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.api.chs.notification.model.GovUkLetterDetailsRequest;
import uk.gov.companieshouse.notification.ChsEmailNotification;
import uk.gov.companieshouse.notification.ChsLetterNotification;

@Component
@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "offsetDateTimeToInstant")
    ChsEmailNotification mapToEmailDetailsRequest(GovUkEmailDetailsRequest govUkEmailDetailsRequest);

    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "offsetDateTimeToInstant")
    ChsLetterNotification mapToLetterDetailsRequest(GovUkLetterDetailsRequest govUkLetterDetailsRequest);

    @Named("offsetDateTimeToInstant")
    static Instant offsetDateTimeToInstant(OffsetDateTime dateTime) {
        return dateTime != null ? dateTime.toInstant() : null;
    }

}
