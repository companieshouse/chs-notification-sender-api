package uk.gov.companieshouse.chs.notification.sender.api.controller;

import static uk.gov.companieshouse.chs.notification.sender.api.ChsNotificationSenderApiApplication.APPLICATION_NAMESPACE;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.chs.notification.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.api.chs.notification.model.GovUkLetterDetailsRequest;
import uk.gov.companieshouse.api.chs.notification.sender.api.NotificationSenderControllerInterface;
import uk.gov.companieshouse.chs.notification.sender.api.exception.NotificationException;
import uk.gov.companieshouse.chs.notification.sender.api.kafka.KafkaProducerService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
public class NotificationSenderController implements NotificationSenderControllerInterface {

    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

    private final KafkaProducerService kafkaProducerService;

    public NotificationSenderController(KafkaProducerService kafkaService) {
        this.kafkaProducerService = kafkaService;
    }

    @Override
    public ResponseEntity<Void> sendEmail(
        @RequestBody final GovUkEmailDetailsRequest govUkEmailDetailsRequest,
        @RequestHeader(value = "X-Request-Id", required = false) final String requestId
    ) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("request_id", Objects.toString(requestId, ""));
        LOG.info("Processing email notification request", logMap);

        kafkaProducerService.sendEmail(govUkEmailDetailsRequest);

        LOG.info("Email notification sent successfully", logMap);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> sendLetter(
        @RequestBody final GovUkLetterDetailsRequest govUkLetterDetailsRequest,
        @RequestHeader(value = "X-Request-Id", required = false) final String requestId
    ) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("request_id", Objects.toString(requestId, ""));
        LOG.info("Processing letter notification request", logMap);

        kafkaProducerService.sendLetter(govUkLetterDetailsRequest);

        LOG.info("Letter notification sent successfully", logMap);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
        final MethodArgumentNotValidException ex
    ) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList());

        Map<String, Object> logMap = new HashMap<>();
        logMap.put("status", HttpStatus.BAD_REQUEST.value());
        logMap.put("errors", errors);
        LOG.error("Validation error", ex, logMap);
        return new ResponseEntity<>(logMap, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<Map<String, Object>> handleNotificationException(
        final NotificationException ex,
        final HttpServletRequest request
    ) {

        Map<String, Object> logMap = new HashMap<>();
        logMap.put("request_id", request.getHeader("X-Request-Id"));
        logMap.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        logMap.put("error", "Failed to process notification");
        logMap.put("message", ex.getMessage());
        LOG.error("Failed to send notification", ex, logMap);
        return new ResponseEntity<>(logMap, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
