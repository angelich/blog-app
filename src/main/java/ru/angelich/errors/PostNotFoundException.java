package ru.angelich.errors;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(String message) {
        super(message);
    }
}
