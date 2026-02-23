package org.microservices.notification_emission.infrastructure.input.sqs.exception;

public class NonRetryableMessageException extends RuntimeException{

    public NonRetryableMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonRetryableMessageException(String message) {
        super(message);
    }
}
