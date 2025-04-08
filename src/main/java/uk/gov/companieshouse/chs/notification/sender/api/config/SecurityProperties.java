package uk.gov.companieshouse.chs.notification.sender.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityProperties {
    private final String apiSecurityPath;
    private final String healthcheckPath;

    public SecurityProperties(
            @Value("${management.endpoints.security.path-mapping.api}") final String apiSecurityPath,
            @Value("${management.endpoints.web.path-mapping.health}") final String healthcheckPath
    ) {
        this.apiSecurityPath = apiSecurityPath;
        this.healthcheckPath = healthcheckPath;
    }

    public String getApiSecurityPath() {
        return apiSecurityPath;
    }

    public String getHealthcheckPath() {
        return healthcheckPath;
    }
}
