package ru.practicum.ewm.ewmService.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.ewmService.exception.ForbiddenException;
import ru.practicum.ewm.ewmService.exception.NotFoundException;
import ru.practicum.ewm.ewmService.model.ErrorResponse;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    private String reason;
    private HttpStatus status;

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(NotFoundException e) {
        reason = "NotFoundException";
        status = HttpStatus.NOT_FOUND;
        e.printStackTrace();
        log.error(e.getMessage(), e);
        return new ErrorResponse(e.getMessage(), reason, getErrors(e), status);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnexpectedException(Throwable e) {
        reason = "Unexpected exception";
        status = HttpStatus.INTERNAL_SERVER_ERROR;
        e.printStackTrace();
        log.error(e.getMessage(), e);
        return new ErrorResponse(e.getMessage(), reason, getErrors(e), status);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        reason = "MethodArgumentNotValidException";
        status = HttpStatus.BAD_REQUEST;
        e.printStackTrace();
        log.error(e.getMessage(), e);
        return new ErrorResponse(e.getMessage(), reason, getErrors(e), status);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingRequestValueException(MissingRequestValueException e) {
        reason = "MissingRequestValueException";
        status = HttpStatus.BAD_REQUEST;
        e.printStackTrace();
        log.error(e.getMessage(), e);
        return new ErrorResponse(e.getMessage(), reason, getErrors(e), status);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenException(ForbiddenException e) {
        reason = "ForbiddenException";
        status = HttpStatus.FORBIDDEN;
        e.printStackTrace();
        log.error(e.getMessage(), e);
        return new ErrorResponse(e.getMessage(), reason, getErrors(e), status);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        reason = "DataIntegrityViolationException";
        status = HttpStatus.BAD_REQUEST;
        e.printStackTrace();
        log.error(e.getMessage(), e);
        return new ErrorResponse(e.getMessage(), reason, getErrors(e), status);
    }

    private List<String> getErrors(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return Collections.singletonList(sw.toString());
    }
}
