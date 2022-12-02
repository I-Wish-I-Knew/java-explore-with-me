package ru.practicum.ewm.statisticService.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.statisticService.model.EndpointHit;
import ru.practicum.ewm.statisticService.model.EndpointHitDto;
import ru.practicum.ewm.statisticService.model.ViewPoints;

@UtilityClass
public class StatMapper {

    public static EndpointHitDto toEndpointHitDto(EndpointHit endpointHit) {
        return EndpointHitDto.builder()
                .id(endpointHit.getId())
                .uri(endpointHit.getUri())
                .app(endpointHit.getApp())
                .ip(endpointHit.getIp())
                .timestamp(endpointHit.getTimestamp())
                .build();
    }

    public static EndpointHit toEndpointHit(EndpointHitDto endpointHitDto) {
        return EndpointHit.builder()
                .uri(endpointHitDto.getUri())
                .app(endpointHitDto.getApp())
                .ip(endpointHitDto.getIp())
                .timestamp(endpointHitDto.getTimestamp())
                .build();
    }

    public static ViewPoints toViewPoints(String app, String uri, Long hits) {
        return ViewPoints.builder()
                .app(app)
                .uri(uri)
                .hits(hits)
                .build();
    }
}
