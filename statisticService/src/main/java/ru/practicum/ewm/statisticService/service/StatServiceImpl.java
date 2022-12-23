package ru.practicum.ewm.statisticService.service;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.statisticService.mapper.StatMapper;
import ru.practicum.ewm.statisticService.model.EndpointHit;
import ru.practicum.ewm.statisticService.model.EndpointHitDto;
import ru.practicum.ewm.statisticService.model.ViewPoints;
import ru.practicum.ewm.statisticService.repository.StatRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatServiceImpl implements StatService {
    private final StatRepository repository;
    private static final String APP = "ewm";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatServiceImpl(StatRepository repository) {
        this.repository = repository;
    }

    @Override
    public EndpointHitDto save(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = StatMapper.toEndpointHit(endpointHitDto);
        return StatMapper.toEndpointHitDto(repository.save(endpointHit));
    }

    @Override
    public List<ViewPoints> get(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(start, FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse(end, FORMATTER);

        if (uris == null || uris.isEmpty()) {
            return new ArrayList<>();
        }

        List<ViewPoints> viewPoints = Boolean.TRUE.equals(unique)
                ? repository.countByTimestampAndUriSAndIpUnique(startTime, endTime, uris)
                : repository.countByTimestampAndUris(startTime, endTime, uris);

        for (String uri : uris) {
            if (viewPoints.stream().noneMatch(vp -> vp.getUri().equals(uri))) {
                viewPoints.add(ViewPoints.builder()
                        .hits(0L)
                        .uri(uri)
                        .app(APP)
                        .build());
            }
        }
        return viewPoints;
    }
}
