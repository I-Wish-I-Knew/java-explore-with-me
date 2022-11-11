package ru.practicum.ewm.ewmService.controller.eventRequests;

import lombok.Getter;
import lombok.ToString;
import ru.practicum.ewm.ewmService.model.event.SortEvents;

import java.time.LocalDateTime;
import java.util.List;

@Getter
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

    private GetAllRequest(String text, List<Long> categories,
                          Boolean paid, LocalDateTime rangeStart,
                          LocalDateTime rangeEnd, Boolean onlyAvailable,
                          SortEvents sort, Integer from, Integer size) {
        this.text = text;
        this.categories = categories;
        this.paid = paid;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.onlyAvailable = onlyAvailable;
        this.sort = sort;
        this.from = from;
        this.size = size;
    }

    public static GetAllRequest of(String text, List<Long> categories,
                                   Boolean paid, LocalDateTime rangeStart,
                                   LocalDateTime rangeEnd, Boolean onlyAvailable,
                                   SortEvents sort, Integer from, Integer size) {
        return new GetAllRequest(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size);
    }
}
