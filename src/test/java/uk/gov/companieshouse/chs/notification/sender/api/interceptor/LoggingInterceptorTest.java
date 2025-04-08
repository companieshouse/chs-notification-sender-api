package uk.gov.companieshouse.chs.notification.sender.api.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.util.LogContext;
import uk.gov.companieshouse.logging.util.LogContextProperties;
import uk.gov.companieshouse.logging.util.LogHelper;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoggingInterceptorTest {

    private LoggingInterceptor interceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private LogContext logContext;

    @BeforeEach
    void setUp() {
        interceptor = new LoggingInterceptor();
        when(request.getSession()).thenReturn(session);
        try (var staticMock = mockStatic(LogHelper.class)) {
            staticMock.when(() -> LogHelper.createNewLogContext(any(HttpServletRequest.class)))
                    .thenReturn(logContext);
        }
    }

    @Test
    void When_PreHandleCalled_Expect_TrueAndLogging() {
        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
        
        verify(session).setAttribute(eq(LogContextProperties.START_TIME_KEY.value()), any(Long.class));
    }

    @Test
    void When_PostHandleCalled_Expect_LoggingCompleted() {
        when(session.getAttribute(LogContextProperties.START_TIME_KEY.value()))
                .thenReturn(System.currentTimeMillis() - 100L);

        interceptor.postHandle(request, response, new Object(), null);

        verify(response).getStatus();
    }
}
