package uk.gov.companieshouse.chs.notification.sender.api.controller;

import static uk.gov.companieshouse.chs.notification.sender.api.ChsNotificationSenderApiApplication.APPLICATION_NAMESPACE;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.NotificationEmailRequest;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.NotificationLetterRequest;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.RequestStatus;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.mapper.EmailRequestMapper;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.mapper.LetterRequestMapper;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.service.NotificationDatabaseService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.logging.util.DataMap;

@RestController
public class NotificationSenderController implements NotificationSenderControllerInterface {

    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAMESPACE);


    private final KafkaProducerService kafkaProducerService;

    private final NotificationDatabaseService notificationDatabaseService;

    public NotificationSenderController(KafkaProducerService kafkaService, NotificationDatabaseService notificationDatabaseService) {
        this.notificationDatabaseService = notificationDatabaseService;
        this.kafkaProducerService = kafkaService;
    }

    @Override
    public ResponseEntity<Void> sendEmail(
            @RequestBody final GovUkEmailDetailsRequest govUkEmailDetailsRequest,
            @RequestHeader(value = "X-Request-Id", required = false) final String requestId
    ) {
        String appId = govUkEmailDetailsRequest.getSenderDetails().getAppId();
        String reference = govUkEmailDetailsRequest.getSenderDetails().getReference();
        var logMap = new DataMap.Builder()
                .requestId(Objects.toString(requestId, ""))
                .build()
                .getLogMap();

        logMap.put("reference", reference);
        logMap.put("app_id", appId);

        LOG.info("Processing email notification request", logMap);

        if (notificationDatabaseService.getEmail(appId, reference).isPresent()) {
            LOG.error("Duplicate email request found", new IllegalStateException(
                    "Email request with same unique reference found in database"), logMap);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        NotificationEmailRequest emailRequest = new NotificationEmailRequest(
                EmailRequestMapper.toDao(govUkEmailDetailsRequest));
        emailRequest.setStatus(RequestStatus.PENDING);

        LOG.debug( "Storing email request in database", logMap);
        notificationDatabaseService.save(emailRequest);

        kafkaProducerService.sendEmail(govUkEmailDetailsRequest);

        LOG.info("Email notification sent successfully", logMap);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> sendLetter(
            @RequestBody final GovUkLetterDetailsRequest govUkLetterDetailsRequest,
            @RequestHeader(value = "X-Request-Id", required = false) final String requestId
    ) {
        String appId = govUkLetterDetailsRequest.getSenderDetails().getAppId();
        String reference = govUkLetterDetailsRequest.getSenderDetails().getReference();
        var logMap = new DataMap.Builder()
                .requestId(Objects.toString(requestId, ""))
                .build()
                .getLogMap();

        logMap.put("reference", reference);
        logMap.put("app_id", appId);

        LOG.info("Processing letter notification request", logMap);

        if (notificationDatabaseService.getLetter(appId, reference).isPresent()) {
            LOG.error("Duplicate letter request found", new IllegalStateException(
                    "Letter request with same unique reference found in database"), logMap);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        NotificationLetterRequest letterRequest = new NotificationLetterRequest(
                LetterRequestMapper.toDao(govUkLetterDetailsRequest));
        letterRequest.setStatus(RequestStatus.PENDING);

        LOG.debug( "Storing letter request in database", logMap);
        notificationDatabaseService.save(letterRequest);

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
}
