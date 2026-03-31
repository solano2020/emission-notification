package org.microservices.notification_emission.application.exception;

public class TransientApplicationException extends RuntimeException{

    public TransientApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransientApplicationException(String message) {
        super(message);
    }
}
