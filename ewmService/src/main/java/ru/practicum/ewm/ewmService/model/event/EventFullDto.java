package ru.practicum.ewm.ewmService.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewm.ewmService.model.category.Category;
import ru.practicum.ewm.ewmService.model.user.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
@Jacksonized
public class EventFullDto {
    private Long id;
    private String annotation;
    private String title;
    private Category category;
    private String description;
    private Location location;
    private Long confirmedRequests;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private LocalDateTime createdOn;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private UserShortDto initiator;
    private final LocalDateTime publishedOn;
    private State state;
    private Long views;
}

