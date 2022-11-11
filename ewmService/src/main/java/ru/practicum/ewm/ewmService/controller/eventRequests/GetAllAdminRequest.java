package ru.practicum.ewm.ewmService.controller.eventRequests;

import lombok.Getter;
import lombok.ToString;
import ru.practicum.ewm.ewmService.model.event.State;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
public class GetAllAdminRequest {
    private List<Long> users;
    private List<State> states;
    private List<Long> categories;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Integer from;
    private Integer size;

    private GetAllAdminRequest(List<Long> users, List<State> states,
                               List<Long> categories, LocalDateTime rangeStart,
                               LocalDateTime rangeEnd, Integer from, Integer size) {
        this.users = users;
        this.states = states;
        this.categories = categories;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.from = from;
        this.size = size;
    }

    public static GetAllAdminRequest of(List<Long> users, List<State> states,
                                        List<Long> categories, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, Integer from, Integer size) {
        return new GetAllAdminRequest(users, states, categories, rangeStart, rangeEnd, from, size);
    }
}
