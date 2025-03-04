# chs-notification-sender-api

## 1.0) Introduction

This module receives a request to send a letter or an email via a REST interface and passes that request on to a Kafka 3 queue.

The design for this module and the service it is a part of is here : https://companieshouse.atlassian.net/wiki/spaces/IDV/pages/5146247171/EMail+Service

## 2.0) Prerequisites

This Microservice has the following dependencies:

- [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Maven](https://maven.apache.org/download.cgi)



# Endpoints

The remainder of this section lists the endpoints that are available in this microservice, and provides links to
detailed documentation about these endpoints e.g. required headers, path variables, query params, request bodies, and
their behaviour.

| Method | Path     | Description                                 | Documentation                                                                                                                                                                |
|--------|----------|---------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| POST   | /lette s | This endpoint can be used to send a letter. | [LLD - Gov.uk Notify Integration API](https://companieshouse.atlassian.net/wiki/spaces/IDV/pages/5162598548/Gov.uk+Notify+Integration+API+chs-gov-uk-notify-integration-api) |
| POST   | /email   | This endpoint can be used to send an email. | [LLD - Gov.uk Notify Integration API](https://companieshouse.atlassian.net/wiki/spaces/IDV/pages/5162598548/Gov.uk+Notify+Integration+API+chs-gov-uk-notify-integration-api) |
| GET |  http://127.0.0.1:9000/actuator/health | this endpoint is used to check that the service is running | |



