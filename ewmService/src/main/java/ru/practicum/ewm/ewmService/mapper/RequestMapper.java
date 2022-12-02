package ru.practicum.ewm.ewmService.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.ewmService.model.event.Event;
import ru.practicum.ewm.ewmService.model.request.Request;
import ru.practicum.ewm.ewmService.model.request.RequestDto;
import ru.practicum.ewm.ewmService.model.user.User;

@UtilityClass
public class RequestMapper {

    public static Request toRequest(Event event, User requester) {
        return Request.builder()
                .event(event)
                .requester(requester)
                .build();
    }

    public static RequestDto toRequestDto(Request request) {
        return RequestDto.builder()
                .status(request.getStatus())
                .created(request.getCreated())
                .id(request.getId())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .build();
    }
}
