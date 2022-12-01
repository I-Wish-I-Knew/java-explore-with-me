package ru.practicum.ewm.ewmService.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.ewmService.model.category.Category;
import ru.practicum.ewm.ewmService.model.event.*;
import ru.practicum.ewm.ewmService.model.user.User;

@UtilityClass
public class EventMapper {

    public static Event toEvent(NewEventDto newEventDto, User initiator, Category category) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .title(newEventDto.getTitle())
                .initiator(initiator)
                .category(category)
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .location(new Location(newEventDto.getLocation().getLat(), newEventDto.getLocation().getLon()))
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .onlyInvited(newEventDto.getOnlyInvited())
                .build();
    }

    public static EventShortDto toEventShortDto(Event event, long confirmedRequests, long views) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .confirmedRequests(confirmedRequests)
                .views(views)
                .title(event.getTitle())
                .eventDate(event.getEventDate())
                .paid(event.getPaid())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .category(event.getCategory())
                .build();
    }

    public static EventFullDto toEventFullDto(Event event, long confirmedRequests, long views) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .confirmedRequests(confirmedRequests)
                .views(views)
                .title(event.getTitle())
                .eventDate(event.getEventDate())
                .paid(event.getPaid())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .category(event.getCategory())
                .description(event.getDescription())
                .location(event.getLocation())
                .createdOn(event.getCreatedOn())
                .requestModeration(event.getRequestModeration())
                .publishedOn(event.getPublishedOn())
                .participantLimit(event.getParticipantLimit())
                .state(event.getState())
                .onlyInvited(event.getOnlyInvited())
                .build();
    }

    public static NewEventDto toNewEventDto(Event event) {
        return NewEventDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .title(event.getTitle())
                .category(event.getCategory().getId())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .location(new NewEventDto.Location(event.getLocation().getLat(), event.getLocation().getLon()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .onlyInvited(event.getOnlyInvited())
                .build();
    }
}
