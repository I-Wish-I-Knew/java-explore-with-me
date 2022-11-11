package ru.practicum.ewm.ewmService.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.ewmService.model.request.RequestDto;
import ru.practicum.ewm.ewmService.service.RequestService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/users/{userId}/requests")
public class RequestController {

    private final RequestService service;

    public RequestController(RequestService service) {
        this.service = service;
    }

    @GetMapping
    public List<RequestDto> getAll(@PathVariable Long userId) {
        log.info("Get all user {} requests", userId);
        return service.getAll(userId);
    }

    @PostMapping
    public RequestDto save(@PathVariable Long userId,
                           @RequestParam Long eventId) {
        log.info("Save request from user {} to event {}", userId, eventId);
        return service.save(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancel(@PathVariable Long userId,
                             @PathVariable Long requestId) {
        log.info("Cancel request {} by user {}", userId, requestId);
        return service.cancel(userId, requestId);
    }
}

