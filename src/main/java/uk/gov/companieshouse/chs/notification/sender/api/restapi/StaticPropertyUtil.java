package uk.gov.companieshouse.chs.notification.sender.api.restapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
class StaticPropertyUtil {

    @Value( "${spring.application.name}" )
    private String applicationNameSpace;

    public static String APPLICATION_NAMESPACE;

    @PostConstruct
    public void init(){
        StaticPropertyUtil.APPLICATION_NAMESPACE = applicationNameSpace;
    }

}
