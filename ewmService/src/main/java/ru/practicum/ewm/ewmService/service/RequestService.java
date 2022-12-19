package ru.practicum.ewm.ewmService.service;

import ru.practicum.ewm.ewmService.model.event.Event;
import ru.practicum.ewm.ewmService.model.request.RequestDto;
import ru.practicum.ewm.ewmService.model.user.User;

import java.util.List;

public interface RequestService {
    List<RequestDto> getAll(Long userId);

    RequestDto save(Long userId, Long eventId);

    RequestDto cancel(Long userId, Long requestId);

    void reserveForInvitedGuest(Event event, User guest);

    void cancelForInvitedGuest(Long eventId, Long guestId);

}
