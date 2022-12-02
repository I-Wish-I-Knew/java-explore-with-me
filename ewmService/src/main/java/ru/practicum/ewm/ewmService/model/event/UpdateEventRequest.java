package ru.practicum.ewm.ewmService.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.ewm.ewmService.utility.Constants.DATE_FORMAT_PATTERN;

@Data
@Builder
@Jacksonized
public class UpdateEventRequest {
    @NotNull
    private Long eventId;
    @Size(min = 20, max = 2000)
    private String annotation;
    @Size(min = 3, max = 120)
    private String title;
    private Long category;
    @Size(min = 20, max = 7000)
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT_PATTERN)
    private LocalDateTime eventDate;
    private Boolean paid;
    private Integer participantLimit;

    @AssertTrue(message = "Event date must be at least two hours after the publication")
    private boolean isAfter() {
        LocalDateTime acceptableTime = LocalDateTime.now().plusHours(2);
        if (eventDate == null) {
            return true;
        }
        return eventDate.isEqual(acceptableTime) || eventDate.isAfter(acceptableTime);
    }
}
