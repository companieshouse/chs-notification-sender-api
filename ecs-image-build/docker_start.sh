#!/bin/bash
#
# Start script for chs-notification-sender-api

PORT=8080

exec java -jar -Dserver.port="${PORT}" "chs-notification-sender-api.jar"