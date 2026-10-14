package uk.gov.companieshouse.chs.notification.sender.api.controller;

import static uk.gov.companieshouse.chs.notification.sender.api.ChsNotificationSenderApiApplication.APPLICATION_NAMESPACE;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.companieshouse.chs.notification.sender.api.exception.AlreadyProcessedException;
import uk.gov.companieshouse.chs.notification.sender.api.exception.NotificationException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.logging.util.DataMap;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

    @Nullable
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        var logMap = new DataMap.Builder()
                .status(Objects.toString(HttpStatus.BAD_REQUEST.value()))
                .errors(errors)
                .build()
                .getLogMap();

        LOG.error("Validation error", ex, logMap);
        return new ResponseEntity<>(logMap, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<Map<String, Object>> handleNotificationException(
            final NotificationException ex,
            final HttpServletRequest request
    ) {
        var logMap = new DataMap.Builder()
                .requestId(request.getHeader("X-Request-Id"))
                .status(Objects.toString(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .errors(List.of("Failed to process notification"))
                .message(ex.getMessage())
                .build()
                .getLogMap();

        LOG.error("Failed to send notification", ex, logMap);
        return new ResponseEntity<>(logMap, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AlreadyProcessedException.class)
    public ResponseEntity<Map<String, Object>> handleAlreadyProcessedException(
            final AlreadyProcessedException ex,
            final HttpServletRequest request
    ) {
        var logMap = new DataMap.Builder()
                .requestId(request.getHeader("X-Request-Id"))
                .status(Objects.toString(HttpStatus.CONFLICT.value()))
                .errors(List.of("Already processed notification"))
                .message(ex.getMessage())
                .build()
                .getLogMap();

        LOG.error(ex.getMessage(), ex, logMap);
        return new ResponseEntity<>(logMap, HttpStatus.CONFLICT);
    }


}
