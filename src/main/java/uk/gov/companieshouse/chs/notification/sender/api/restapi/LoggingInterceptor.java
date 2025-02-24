package uk.gov.companieshouse.chs.notification.sender.api.restapi;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import uk.gov.companieshouse.chs.notification.sender.api.utils.StaticPropertyUtil;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.logging.util.RequestLogger;

@Component
class LoggingInterceptor implements HandlerInterceptor, RequestLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticPropertyUtil.APPLICATION_NAMESPACE);

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        logStartRequestProcessing(request, LOGGER);
        return true;
    }

    @Override
    public void postHandle(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final Object handler, final ModelAndView modelAndView) {
        logEndRequestProcessing(request, response, LOGGER);
    }

}
