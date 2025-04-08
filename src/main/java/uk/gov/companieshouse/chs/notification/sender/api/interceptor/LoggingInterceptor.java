package uk.gov.companieshouse.chs.notification.sender.api.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.logging.util.RequestLogger;

import static uk.gov.companieshouse.chs.notification.sender.api.config.ApplicationConfig.APPLICATION_NAMESPACE;

@Component
public class LoggingInterceptor implements HandlerInterceptor, RequestLogger {
    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

    @Override
    public boolean preHandle(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final Object handler
    ) {
        logStartRequestProcessing(request, LOG);
        return true;
    }

    @Override
    public void postHandle(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final Object handler,
            final ModelAndView modelAndView
    ) {
        logEndRequestProcessing(request, response, LOG);
    }
}
