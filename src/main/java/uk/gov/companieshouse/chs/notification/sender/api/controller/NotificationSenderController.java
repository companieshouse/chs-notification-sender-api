package uk.gov.companieshouse.chs.notification.sender.api.controller;

import static java.lang.String.format;
import static uk.gov.companieshouse.chs.notification.sender.api.ChsNotificationSenderApiApplication.APPLICATION_NAMESPACE;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.companieshouse.api.chs.notification.sender.api.NotificationSenderControllerInterface;
import uk.gov.companieshouse.api.chs.notification.sender.model.GovUkEmailDetailsRequest;
import uk.gov.companieshouse.api.chs.notification.sender.model.GovUkLetterDetailsRequest;
import uk.gov.companieshouse.chs.notification.sender.api.exception.AlreadyProcessedException;
import uk.gov.companieshouse.chs.notification.sender.api.kafka.KafkaProducerService;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.NotificationEmailRequest;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.NotificationLetterRequest;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.RequestStatus;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.mapper.EmailRequestMapper;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.model.mapper.LetterRequestMapper;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.service.NotificationDatabaseService;
import uk.gov.companieshouse.chs.notification.sender.api.storage.AttachmentStorageService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.logging.util.DataMap;

@RestController
public class NotificationSenderController implements NotificationSenderControllerInterface {

    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

    private final KafkaProducerService kafkaProducerService;
    private final AttachmentStorageService attachmentStorageService;

    private final NotificationDatabaseService notificationDatabaseService;

    public NotificationSenderController(KafkaProducerService kafkaService,
                                        NotificationDatabaseService notificationDatabaseService,
                                        AttachmentStorageService attachmentStorageService) {
        this.notificationDatabaseService = notificationDatabaseService;
        this.kafkaProducerService = kafkaService;
        this.attachmentStorageService = attachmentStorageService;
    }

    @Override
    public ResponseEntity<Void> sendEmail(
            @RequestBody final GovUkEmailDetailsRequest govUkEmailDetailsRequest,
            @RequestHeader(value = "X-Request-Id", required = false) final String requestId) {

        var logMap = buildLogMap(
                requestId,
                govUkEmailDetailsRequest.getSenderDetails().getReference(),
                govUkEmailDetailsRequest.getSenderDetails().getAppId());

        LOG.info("Processing email notification request", logMap);

        checkIfAlreadyProcessed(govUkEmailDetailsRequest);

        saveEmailRequest(govUkEmailDetailsRequest, logMap);
        kafkaProducerService.sendEmail(govUkEmailDetailsRequest);

        LOG.info("Email notification sent successfully", logMap);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(
            value = "/notification-sender/email",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public ResponseEntity<Void> sendEmailWithAttachment(
            @RequestPart(value = "request") final GovUkEmailDetailsRequest govUkEmailDetailsRequest,
            @RequestPart(value = "file") final MultipartFile attachment,
            @RequestHeader(value = "X-Request-Id", required = false) final String requestId) {

        var logMap = buildLogMap(
                requestId,
                govUkEmailDetailsRequest.getSenderDetails().getReference(),
                govUkEmailDetailsRequest.getSenderDetails().getAppId());

        LOG.info("Processing email with attachment notification request", logMap);

        checkIfAlreadyProcessed(govUkEmailDetailsRequest);

        saveAttachment(govUkEmailDetailsRequest, attachment);
        saveEmailRequest(govUkEmailDetailsRequest, logMap);

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
        var logMap = buildLogMap(requestId, reference, appId);

        LOG.info("Processing letter notification request", logMap);
        checkIfAlreadyProcessed(govUkLetterDetailsRequest);

        NotificationLetterRequest letterRequest = new NotificationLetterRequest(
                LetterRequestMapper.toDao(govUkLetterDetailsRequest));
        letterRequest.setStatus(RequestStatus.PENDING);

        LOG.debug( "Storing letter request in database", logMap);
        notificationDatabaseService.save(letterRequest);

        kafkaProducerService.sendLetter(govUkLetterDetailsRequest);

        LOG.info("Letter notification sent successfully", logMap);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private void saveAttachment(GovUkEmailDetailsRequest govUkEmailDetailsRequest,
                                MultipartFile attachment) {
        String attachmentId = attachmentStorageService.storeAttachment(govUkEmailDetailsRequest, attachment);
        govUkEmailDetailsRequest.getEmailDetails().setAttachmentId(attachmentId);
        LOG.debug(format("Stored attachment with id %s", attachmentId));
    }

    private void checkIfAlreadyProcessed(GovUkEmailDetailsRequest govUkEmailDetailsRequest) {
        Optional<NotificationEmailRequest> email = notificationDatabaseService
                .getEmail(
                        govUkEmailDetailsRequest.getSenderDetails().getAppId(),
                        govUkEmailDetailsRequest.getSenderDetails().getReference());
        if (email.isPresent()) {
            throw new AlreadyProcessedException(govUkEmailDetailsRequest);
        }
    }

    private void checkIfAlreadyProcessed(GovUkLetterDetailsRequest govUkLetterDetailsRequest) {
        Optional<NotificationLetterRequest> letter = notificationDatabaseService
                .getLetter(
                        govUkLetterDetailsRequest.getSenderDetails().getAppId(),
                        govUkLetterDetailsRequest.getSenderDetails().getReference());
        if (letter.isPresent()) {
            throw new AlreadyProcessedException(govUkLetterDetailsRequest);
        }
    }

    private void saveEmailRequest(GovUkEmailDetailsRequest govUkEmailDetailsRequest, Map<String, Object> logMap) {
        NotificationEmailRequest emailRequest = new NotificationEmailRequest(
                EmailRequestMapper.toDao(govUkEmailDetailsRequest));
        emailRequest.setStatus(RequestStatus.PENDING);

        LOG.debug( "Storing email request in database", logMap);
        notificationDatabaseService.save(emailRequest);
    }

    private static Map<String, Object> buildLogMap(String requestId, String reference, String appId) {
        var logMap = new DataMap.Builder()
                .requestId(Objects.toString(requestId, ""))
                .build()
                .getLogMap();

        logMap.put("reference", reference);
        logMap.put("app_id", appId);
        return logMap;
    }
}
