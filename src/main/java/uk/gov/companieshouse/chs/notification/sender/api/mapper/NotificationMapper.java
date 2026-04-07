package uk.gov.companieshouse.chs.notification.sender.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.chs.notification.sender.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.api.chs.notification.sender.model.GovUkLetterDetailsRequest;
import uk.gov.companieshouse.notification.ChsEmailNotification;
import uk.gov.companieshouse.notification.ChsLetterNotification;

@Component
@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "senderDetails.appId", target = "appId")
    @Mapping(source = "senderDetails.reference", target = "reference")
    ChsEmailNotification mapToEmailDetailsRequest(GovUkEmailDetailsRequest govUkEmailDetailsRequest);

    @Mapping(source = "senderDetails.appId", target = "appId")
    @Mapping(source = "senderDetails.reference", target = "reference")
    ChsLetterNotification mapToLetterDetailsRequest(GovUkLetterDetailsRequest govUkLetterDetailsRequest);
}
