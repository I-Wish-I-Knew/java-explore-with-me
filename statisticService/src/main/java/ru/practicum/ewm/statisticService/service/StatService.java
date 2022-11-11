package ru.practicum.ewm.statisticService.service;

import ru.practicum.ewm.statisticService.model.EndpointHitDto;
import ru.practicum.ewm.statisticService.model.ViewPoints;

import java.util.List;

public interface StatService {

    EndpointHitDto save(EndpointHitDto endpointHitDto);

    List<ViewPoints> get(String start, String end, List<String> uris, Boolean unique);
}
