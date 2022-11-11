package ru.practicum.ewm.statisticService.service;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.statisticService.mapper.StatMapper;
import ru.practicum.ewm.statisticService.model.EndpointHit;
import ru.practicum.ewm.statisticService.model.EndpointHitDto;
import ru.practicum.ewm.statisticService.model.ViewPoints;
import ru.practicum.ewm.statisticService.repository.StatRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatServiceImpl implements StatService {

    private final StatRepository repository;
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
        LocalDateTime startTime = LocalDateTime.parse(decode(start), FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse(decode(end), FORMATTER);

        if (uris == null || uris.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, Long> hits = new HashMap<>();
        Map<String, String> apps = new HashMap<>();

        for (Object[] ob : repository.findApps(uris)) {
            apps.put((String) ob[0], (String) ob[1]);
        }
        if (Boolean.FALSE.equals(unique)) {
            for (String uri : uris) {
                hits.put(uri, repository.countByTimestampAfterAndTimestampBeforeAndUri(startTime, endTime, uri));
            }
        } else {
            for (String uri : uris) {
                hits.put(uri, repository.countByTimestampAndUriAndIpUnique(startTime, endTime, uri));
            }
        }

        List<ViewPoints> viewPoints = new ArrayList<>();

        hits.keySet().forEach(uri -> viewPoints.add(StatMapper.toViewPoints(apps.get(uri), uri, hits.get(uri))));
        return viewPoints;
    }

    private String decode(String encoded) {
        return java.net.URLDecoder.decode(encoded, StandardCharsets.UTF_8);
    }
}
