package ru.practicum.ewm.ewmService.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private String reason;
    private List<String> errors;
    private HttpStatus status;
    private Timestamp timestamp;

    public ErrorResponse(String message, String reason, List<String> errors, HttpStatus status) {
        this.message = message;
        this.reason = reason;
        this.errors = errors;
        this.status = status;
        this.timestamp = Timestamp.valueOf(LocalDateTime.now());
    }
}
