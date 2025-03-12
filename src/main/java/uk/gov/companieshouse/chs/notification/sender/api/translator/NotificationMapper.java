package uk.gov.companieshouse.chs.notification.sender.api.translator;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkLetterDetailsRequest;
import uk.gov.companieshouse.notification.ChsEmailNotification;
import uk.gov.companieshouse.notification.ChsLetterNotification;

import java.time.Instant;
import java.time.OffsetDateTime;

@Component
@Mapper(componentModel = "spring")
interface NotificationMapper {

    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "offsetDateTimeToInstant")
    ChsEmailNotification mapToEmailDetailsRequest(GovUkEmailDetailsRequest govUkEmailDetailsRequest);

    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "offsetDateTimeToInstant")
    ChsLetterNotification mapToLetterDetailsRequest(GovUkLetterDetailsRequest govUkLetterDetailsRequest);

    @Named("offsetDateTimeToInstant")
    static Instant offsetDateTimeToInstant(OffsetDateTime dateTime) {
        return dateTime != null ? dateTime.toInstant() : null;
    }
}
