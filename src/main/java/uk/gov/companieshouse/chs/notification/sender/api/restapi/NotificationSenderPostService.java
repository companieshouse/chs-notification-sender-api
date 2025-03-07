package uk.gov.companieshouse.chs.notification.sender.api.restapi;

import uk.gov.companieshouse.api.sdk.impl.ApiClientServiceImpl;

public class NotificationSenderPostService {
    ApiClientServiceImpl apiClientService;

    public NotificationSenderPostService(ApiClientServiceImpl apiClientService) {
        this.apiClientService = apiClientService;
    }

    public void govUkCaller() {
        //apiClientService.getNotificationApiUrl();
        //logic cross-reference with the other one
    }
}
