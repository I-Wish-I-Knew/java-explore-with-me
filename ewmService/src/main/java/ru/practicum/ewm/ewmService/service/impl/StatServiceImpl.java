package ru.practicum.ewm.ewmService.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.ewmService.model.event.Event;
import ru.practicum.ewm.ewmService.model.statModel.EndpointHitDto;
import ru.practicum.ewm.ewmService.model.statModel.ViewPoints;
import ru.practicum.ewm.ewmService.service.StatService;
import ru.practicum.ewm.ewmService.statClient.StatClient;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatServiceImpl implements StatService {

    private static final String APP = "ewm";
    private static final String BASE_URI_EVENT_VIEW = "/events/{%d}";
    private final StatClient client;

    public StatServiceImpl(StatClient client) {
        this.client = client;
    }

    @Override
    public void saveHit(HttpServletRequest request) {
        client.save(createEndpointHit(request.getRequestURI(), request.getRemoteAddr()));
    }

    @Override
    public Map<Long, Long> getViewsForEvents(List<Event> events, Boolean unique) {
        Optional<Event> firstEvent = events.stream()
                .min(Comparator.comparing(Event::getEventDate));

        if (firstEvent.isEmpty()) {
            return new HashMap<>();
        }

        String start = getDateForRequest(firstEvent.get().getCreatedOn());
        String end = getDateForRequest(LocalDateTime.now());
        List<String> uris = getUris(events);
        List<ViewPoints> viewPoints = client.get(start, end, uris, unique);
        Map<Long, Long> eventViews = new HashMap<>();

        viewPoints.forEach(viewPoint -> eventViews.put(getIdFromUri(viewPoint.getUri()), viewPoint.getHits()));

        return eventViews;
    }

    @Override
    public Long getViewsForEvent(Event event, Boolean unique) {
        List<ViewPoints> views = client.get(getDateForRequest(event.getCreatedOn()),
                getDateForRequest(LocalDateTime.now()),
                List.of(String.format(BASE_URI_EVENT_VIEW, event.getId())),
                unique);

        if (views.isEmpty()) {
            return 0L;
        }

        return views.get(0).getHits();
    }

    private List<String> getUris(List<Event> events) {
        return events.stream()
                .map(event -> String.format(BASE_URI_EVENT_VIEW, event.getId()))
                .collect(Collectors.toList());
    }

    private EndpointHitDto createEndpointHit(String uri, String ip) {
        return EndpointHitDto.builder()
                .app(APP)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private Long getIdFromUri(String uri) {
        return Long.parseLong(StringUtils.getDigits(uri));
    }

    private String getDateForRequest(LocalDateTime date) {
        return date.withNano(0).toString().replace("T", " ");
    }
}
