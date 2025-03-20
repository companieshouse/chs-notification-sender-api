# chs-notification-sender-api

## 1.0) Introduction

This module receives a request to send a letter or an email via a REST interface and passes that request on to a Kafka 3
queue.

The design for this module and the service it is a part of is
here : https://companieshouse.atlassian.net/wiki/spaces/IDV/pages/5146247171/EMail+Service

## 2.0) Prerequisites

This Microservice has the following dependencies:

- [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Maven](https://maven.apache.org/download.cgi)

# OWASP Dependency check

to run a check for dependency security vulnerabilities run the following command:

```shell
mvn dependency-check:check
```

# List Dependencies

```shell
mvn dependency:tree
```

# Endpoints

The remainder of this section lists the endpoints that are available in this microservice, and provides links to
detailed documentation about these endpoints e.g. required headers, path variables, query params, request bodies, and
their behaviour.

| Method | Path                                    | Description                                                | Documentation                                                                                                                                              |
|--------|-----------------------------------------|------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| POST   | /letter                                 | This endpoint can be used to send a letter.                | [LLD - Kafka3 Notification API](https://companieshouse.atlassian.net/wiki/spaces/IDV/pages/5162008722/Kafka3+Notification+API+chs-notification-sender-api) |
| POST   | /email                                  | This endpoint can be used to send an email.                | [LLD - Kafka3 Notification API](https://companieshouse.atlassian.net/wiki/spaces/IDV/pages/5162008722/Kafka3+Notification+API+chs-notification-sender-api) |
| GET    | http://127.0.0.1:9000/chs-notification-sender-api/healthcheck   | this endpoint is used to check that the service is running |                                                                                                                                                            |
| GET    | http://127.0.0.1:9000/chs-notification-sender-api/info     |                                                            |                                                                                                                                                            |
| GET    | http://127.0.0.1:9000/chs-notification-sender-api/mappings |                                                            |                                                                                                                                                            |
