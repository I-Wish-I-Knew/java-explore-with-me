package ru.practicum.ewm.ewmService.controller.eventRequests;

import lombok.*;
import ru.practicum.ewm.ewmService.model.event.SortEvents;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class GetAllRequest {
    private String text;
    private List<Long> categories;
    private Boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable;
    private SortEvents sort;
    private Integer from;
    private Integer size;
}
