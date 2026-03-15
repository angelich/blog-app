package ru.angelich.errors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;



import static java.util.Collections.emptySet;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({PostNotFoundException.class, CommentNotFoundException.class})
    public ErrorResponse handleValidationException(RuntimeException e) {
        log.warn("Bad request, message={}", e.getMessage());
        return new ErrorResponse() {
            @Override
            public HttpStatusCode getStatusCode() {
                return BAD_REQUEST;
            }

            @Override
            public ProblemDetail getBody() {
                return ProblemDetail.forStatusAndDetail(BAD_REQUEST, e.getMessage());
            }
        };
    }

    @ExceptionHandler
    public ErrorResponse handleOtherExceptions(Throwable e) {
        log.error("Internal error, message={}", e.getMessage());
        return new ErrorResponse() {
            @Override
            public HttpStatusCode getStatusCode() {
                return INTERNAL_SERVER_ERROR;
            }

            @Override
            public ProblemDetail getBody() {
                return ProblemDetail.forStatusAndDetail(INTERNAL_SERVER_ERROR, e.getMessage());
            }
        };
    }
}