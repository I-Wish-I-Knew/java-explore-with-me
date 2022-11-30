package ru.practicum.ewm.ewmService.controller.eventRequests;

import lombok.*;
import ru.practicum.ewm.ewmService.model.event.State;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class GetAllAdminRequest {
    private List<Long> users;
    private List<State> states;
    private List<Long> categories;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Integer from;
    private Integer size;
}
