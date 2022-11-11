package ru.practicum.ewm.statisticService.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.statisticService.model.ErrorResponse;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    private String reason;
    private HttpStatus status;

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnexpectedException(Throwable e) {
        reason = "Unexpected exception";
        status = HttpStatus.INTERNAL_SERVER_ERROR;
        e.printStackTrace();
        log.error(e.getMessage(), e);
        return new ErrorResponse(e.getMessage(), reason, getErrors(e), status, Timestamp.valueOf(LocalDateTime.now()));
    }

    private List<String> getErrors(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return Collections.singletonList(sw.toString());
    }
}
