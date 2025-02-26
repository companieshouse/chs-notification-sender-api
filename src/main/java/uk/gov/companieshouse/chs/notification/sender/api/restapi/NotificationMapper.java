package uk.gov.companieshouse.chs.notification.sender.api.restapi;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.api.chs_notification_sender.model.GovUkLetterDetailsRequest;

@Component
@Mapper(componentModel = "spring")
interface NotificationMapper {

    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

 /*   @Mapping(target = "senderDetails", source = "senderDetails")
    @Mapping(target = "recipientDetails", source = "recipientDetails")
    @Mapping(target = "emailDetails", source = "emailDetails")
    @Mapping(target = "createdAt", source = "createdAt")*/
    EmailDetailsRequest mapToEmailDetailsRequest(GovUkEmailDetailsRequest govUkEmailDetailsRequest);

    LetterDetailsRequest mapToLetterDetailsRequest(GovUkLetterDetailsRequest govUkLetterDetailsRequest);

}
