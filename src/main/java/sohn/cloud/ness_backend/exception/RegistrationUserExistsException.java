package sohn.cloud.ness_backend.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class RegistrationUserExistsException extends Exception{
    String message;

    public RegistrationUserExistsException(String message) {
        this.message = message;
    }
}
