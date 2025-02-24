package uk.gov.companieshouse.chs.notification.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StaticPropertyUtil {
    @Value( "${spring.application.name}" )
    private String applicationNameSpace;

    public static String APPLICATION_NAMESPACE;

    @PostConstruct
    public void init(){
        StaticPropertyUtil.APPLICATION_NAMESPACE = applicationNameSpace;
    }

}
