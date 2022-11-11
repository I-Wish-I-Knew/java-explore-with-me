package ru.practicum.ewm.ewmService.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.ewmService.model.category.Category;
import ru.practicum.ewm.ewmService.model.user.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
public class EventShortDto {
    private Long id;
    private String annotation;
    private String title;
    private Category category;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Long confirmedRequests;
    private Boolean paid;
    private UserShortDto initiator;
    private Long views;
}
