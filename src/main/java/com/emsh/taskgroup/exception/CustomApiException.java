package com.emsh.taskgroup.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.time.ZonedDateTime;

@Getter
@Setter
public class CustomApiException extends Exception {

    private final int httpStatusCode;
    private final HttpStatus httpStatus;
    private final ZonedDateTime timestamp;

    public CustomApiException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
        httpStatusCode = httpStatus.value();
        timestamp = ZonedDateTime.now();
    }
}
