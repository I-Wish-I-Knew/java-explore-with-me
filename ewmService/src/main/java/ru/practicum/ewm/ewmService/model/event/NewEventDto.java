package ru.practicum.ewm.ewmService.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.ewm.ewmService.utility.Constants.DATE_FORMAT_PATTERN;

@Data
@Builder
@Jacksonized
public class NewEventDto {
    private Long id;
    @Size(min = 20, max = 7000)
    @NotBlank
    private String annotation;
    @Size(min = 3, max = 120)
    @NotBlank
    private String title;
    @NotNull
    private Long category;
    @Size(min = 20, max = 7000)
    @NotBlank
    private String description;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT_PATTERN)
    private LocalDateTime eventDate;
    @NotNull
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;

    @AssertTrue(message = "Event date must be at least two hours after the publication")
    private boolean isAfter() {
        LocalDateTime acceptableTime = LocalDateTime.now().plusHours(2);
        return eventDate.isEqual(acceptableTime) || eventDate.isAfter(acceptableTime);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Location {
        private Double lat;
        private Double lon;
    }
}
