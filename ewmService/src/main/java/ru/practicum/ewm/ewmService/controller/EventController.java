package ru.practicum.ewm.ewmService.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.ewmService.controller.eventRequests.GetAllAdminRequest;
import ru.practicum.ewm.ewmService.controller.eventRequests.GetAllRequest;
import ru.practicum.ewm.ewmService.model.event.*;
import ru.practicum.ewm.ewmService.model.request.RequestDto;
import ru.practicum.ewm.ewmService.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
public class EventController {

    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @GetMapping("/events")
    public List<EventShortDto> getAll(@RequestParam(required = false) String text,
                                      @RequestParam(required = false) List<Long> categories,
                                      @RequestParam(required = false) Boolean paid,
                                      @RequestParam(required = false)
                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                      @RequestParam(required = false)
                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                      @RequestParam(required = false) Boolean onlyAvailable,
                                      @RequestParam(required = false) SortEvents sort,
                                      @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                      @RequestParam(defaultValue = "10") @Positive Integer size,
                                      HttpServletRequest request) {
        GetAllRequest req = GetAllRequest.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .from(from)
                .size(size)
                .build();
        log.info("Get all events with filters {}", req);
        return service.getAll(req, request);
    }

    @GetMapping("/events/{id}")
    public EventFullDto get(@PathVariable Long id,
                            HttpServletRequest request) {
        log.info("Get event {}", id);
        return service.get(id, request);
    }

    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> getAllByUser(@PathVariable Long userId,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Get all events by user {} from {} size {}", userId, from, size);
        return service.getAllByUser(userId, from, size);
    }

    @PatchMapping("/users/{userId}/events")
    public EventFullDto update(@PathVariable Long userId,
                               @RequestBody @Valid UpdateEventRequest updateEventRequest) {
        log.info("Update event {}, fields {}", updateEventRequest.getEventId(), updateEventRequest);
        return service.update(updateEventRequest, userId);
    }

    @PostMapping("/users/{userId}/events")
    public EventFullDto save(@PathVariable Long userId,
                             @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Save event {} by user {}", newEventDto, userId);
        return service.save(newEventDto, userId);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getByUser(@PathVariable Long userId,
                                  @PathVariable Long eventId) {
        log.info("Get event {} by user {}", eventId, userId);
        return service.getByUser(eventId, userId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto cancel(@PathVariable Long userId,
                               @PathVariable Long eventId) {
        log.info("Cancel event {} by user {}", eventId, userId);
        return service.cancel(eventId, userId);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<RequestDto> getRequests(@PathVariable Long userId,
                                        @PathVariable Long eventId) {
        log.info("Get requests for event {} by user {}", eventId, userId);
        return service.getRequests(eventId, userId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public RequestDto confirmRequest(@PathVariable Long userId,
                                     @PathVariable Long eventId,
                                     @PathVariable Long reqId) {
        log.info("Confirm request {} for event {} by user {}", reqId, eventId, userId);
        return service.confirmRequest(eventId, userId, reqId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests/{reqId}/reject")
    public RequestDto rejectRequest(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @PathVariable Long reqId) {
        log.info("Reject request {} for event {} by user {}", reqId, eventId, userId);
        return service.rejectRequest(eventId, userId, reqId);
    }

    @GetMapping("/admin/events")
    public List<EventFullDto> getAllAdmin(@RequestParam(required = false) List<Long> users,
                                          @RequestParam(required = false) List<State> states,
                                          @RequestParam(required = false) List<Long> categories,
                                          @RequestParam(required = false)
                                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                          @RequestParam(required = false)
                                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                          @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                          @RequestParam(defaultValue = "10") @Positive Integer size) {
        GetAllAdminRequest req = GetAllAdminRequest.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();
        log.info("Get all events by admin with filters {}", req);
        return service.getAllAdmin(req);
    }

    @PutMapping("/admin/events/{eventId}")
    public EventFullDto editEvent(@RequestBody NewEventDto newEventDto,
                                  @PathVariable Long eventId) {
        log.info("Edit event {} by admin, fields {}", eventId, newEventDto);
        return service.editEvent(newEventDto, eventId);
    }

    @PatchMapping("/admin/events/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable Long eventId) {

        log.info("Publish event {} by admin", eventId);
        return service.publishEvent(eventId);
    }

    @PatchMapping("/admin/events/{eventId}/reject")
    public EventFullDto rejectEvent(@PathVariable Long eventId) {
        log.info("Reject event {} by admin", eventId);
        return service.rejectEvent(eventId);
    }
}
