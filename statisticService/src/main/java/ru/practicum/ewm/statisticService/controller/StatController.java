package ru.practicum.ewm.statisticService.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.statisticService.model.EndpointHitDto;
import ru.practicum.ewm.statisticService.model.ViewPoints;
import ru.practicum.ewm.statisticService.service.StatService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
public class StatController {

    private final StatService service;

    public StatController(StatService service) {
        this.service = service;
    }

    @PostMapping("/hit")
    public EndpointHitDto save(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        log.info("Save endpoint hit {}", endpointHitDto);
        return service.save(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewPoints> get(@RequestParam String start,
                                @RequestParam String end,
                                @RequestParam(required = false) List<String> uris,
                                @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Get view points  of uris {} unique = {}", uris, unique);
        return service.get(start, end, uris, unique);
    }
}
