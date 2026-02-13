#!/bin/sh
set -eu

QUEUE_NAME="${QUEUE_NAME:-emission-notification-queue}"
DLQ_NAME="${DLQ_NAME:-emission-notification-dlq}"
MAX_RECEIVE_COUNT="${MAX_RECEIVE_COUNT:-3}"

echo "[localstack-init] Creating DLQ: ${DLQ_NAME}"
DLQ_URL="$(awslocal sqs create-queue --queue-name "${DLQ_NAME}" --query 'QueueUrl' --output text)"
DLQ_ARN="$(awslocal sqs get-queue-attributes --queue-url "${DLQ_URL}" --attribute-names QueueArn --query 'Attributes.QueueArn' --output text)"

echo "[localstack-init] Creating main queue: ${QUEUE_NAME}"
MAIN_URL="$(awslocal sqs create-queue --queue-name "${QUEUE_NAME}" --query 'QueueUrl' --output text)"

echo "[localstack-init] Configuring redrive policy (maxReceiveCount=${MAX_RECEIVE_COUNT})"
ATTRIBUTES_JSON="$(printf '{"RedrivePolicy":"{\\"deadLetterTargetArn\\":\\"%s\\",\\"maxReceiveCount\\":\\"%s\\"}"}' "${DLQ_ARN}" "${MAX_RECEIVE_COUNT}")"
awslocal sqs set-queue-attributes \
  --queue-url "${MAIN_URL}" \
  --attributes "${ATTRIBUTES_JSON}"

echo "[localstack-init] Done. Queue URL: ${MAIN_URL}"
