package ru.practicum.ewm.ewmService.service;

import ru.practicum.ewm.ewmService.model.event.Event;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface StatService {

    void saveHit(HttpServletRequest request);

    Map<Long, Long> getViewsForEvents(List<Event> events, Boolean unique);

    Long getViewsForEvent(Event event, Boolean unique);
}
