package org.microservices.notification_emission.domain.exception;

public class EmissionNotFoundException extends RuntimeException{

    public EmissionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmissionNotFoundException(String message) {
        super(message);
    }
}
