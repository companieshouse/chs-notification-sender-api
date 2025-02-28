package uk.gov.companieshouse.chs.notification.sender.api.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.api.interceptor.InternalUserInterceptor;
import uk.gov.companieshouse.chs.notification.sender.api.restapi.LoggingInterceptor;
import uk.gov.companieshouse.chs.notification.sender.api.utils.StaticPropertyUtil;

@Configuration
class InterceptorConfig implements WebMvcConfigurer {

    private final LoggingInterceptor loggingInterceptor;

    public InterceptorConfig(final LoggingInterceptor loggingInterceptor) {
        this.loggingInterceptor = loggingInterceptor;
    }

    @Override
    public void addInterceptors(@NonNull final InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor);
        registry.addInterceptor(new InternalUserInterceptor(StaticPropertyUtil.APPLICATION_NAMESPACE));
    }

}
