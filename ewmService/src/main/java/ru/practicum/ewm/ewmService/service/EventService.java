package ru.practicum.ewm.ewmService.service;

import ru.practicum.ewm.ewmService.controller.eventRequests.GetAllAdminRequest;
import ru.practicum.ewm.ewmService.controller.eventRequests.GetAllRequest;
import ru.practicum.ewm.ewmService.model.event.*;
import ru.practicum.ewm.ewmService.model.request.RequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    List<EventShortDto> getAll(GetAllRequest getAllEventsRequest, HttpServletRequest request);

    EventFullDto get(Long id, HttpServletRequest request);

    List<EventShortDto> getAllByUser(Long id, Integer from, Integer size);

    EventFullDto update(UpdateEventRequest updateEventRequest, Long userId);

    EventFullDto save(NewEventDto newEventDto, Long userId);

    EventFullDto getByUser(Long id, Long userId);

    EventFullDto cancel(Long id, Long userId);

    List<RequestDto> getRequests(Long id, Long userId);

    RequestDto confirmRequest(Long id, Long userId, Long reqId);

    RequestDto rejectRequests(Long id, Long userId, Long reqId);

    List<EventFullDto> getAllAdmin(GetAllAdminRequest getAllAdminRequest);

    EventFullDto editEvent(NewEventDto newEventDto, Long id);

    EventFullDto publishEvent(Long id);

    EventFullDto rejectEvent(Long id);

    List<EventShortDto> convertToListEventShortDto(List<Event> events, Boolean uniqRequests);
}
