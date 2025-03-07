package uk.gov.companieshouse.chs.notification.sender.api.apiclient;

import org.springframework.beans.factory.annotation.Value;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

public class ApiClientServiceImpl implements ApiClientService {

    @Value("${chs-gov-uk-notify-integration-api}")
    String notificationApiUrl;

    @Override
    public InternalApiClient getNotificationApiUrl() {
        InternalApiClient notificationApiClient = ApiSdkManager.getPrivateSDK();
        notificationApiClient.setInternalBasePath(notificationApiUrl);
        notificationApiClient.setBasePath(notificationApiUrl);
        return notificationApiClient;
    }
}
