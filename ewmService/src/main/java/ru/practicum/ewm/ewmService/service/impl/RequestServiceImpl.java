package ru.practicum.ewm.ewmService.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.ewmService.exception.AlreadyExistsException;
import ru.practicum.ewm.ewmService.exception.ForbiddenException;
import ru.practicum.ewm.ewmService.exception.NotFoundException;
import ru.practicum.ewm.ewmService.mapper.RequestMapper;
import ru.practicum.ewm.ewmService.model.event.Event;
import ru.practicum.ewm.ewmService.model.event.State;
import ru.practicum.ewm.ewmService.model.request.Request;
import ru.practicum.ewm.ewmService.model.request.RequestDto;
import ru.practicum.ewm.ewmService.model.request.StateRequest;
import ru.practicum.ewm.ewmService.model.user.User;
import ru.practicum.ewm.ewmService.repository.EventRepository;
import ru.practicum.ewm.ewmService.repository.RequestRepository;
import ru.practicum.ewm.ewmService.repository.UserRepository;
import ru.practicum.ewm.ewmService.service.RequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.ewm.ewmService.utility.Constants.REQUEST_NOT_FOUND;

@Transactional(readOnly = true)
@Service
public class RequestServiceImpl implements RequestService {
    private final RequestRepository repository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public RequestServiceImpl(RequestRepository repository, UserRepository userRepository, EventRepository eventRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public List<RequestDto> getAll(Long userId) {
        List<Request> requests = repository.findAllByRequesterId(userId);
        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public RequestDto save(Long userId, Long eventId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));
        Event event = eventRepository.findByIdAndState(eventId, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(String.format("Published event with id=%d was not found", eventId)));
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ForbiddenException(String.format("User %d is an initiator of event %d", userId, eventId));
        }
        if (Boolean.TRUE.equals(repository.existsByEventIdAndRequesterId(eventId, userId))) {
            throw new AlreadyExistsException(String.format("User %d has already sent a request for event %d",
                    userId, eventId));
        }

        Request request = RequestMapper.toParticipationRequest(event, requester);
        request.setCreated(LocalDateTime.now());

        if (event.getParticipantLimit() == null || event.getParticipantLimit().equals(0)
                && Boolean.FALSE.equals(event.getRequestModeration())) {
            request.setStatus(StateRequest.CONFIRMED);
        } else if (Objects.equals(event.getParticipantLimit(),
                repository.countAllByStatusAndEventId(StateRequest.CONFIRMED, eventId))) {
            request.setStatus(StateRequest.REJECTED);
            eventRepository.save(event);
            throw new ForbiddenException(String.format("Participants limit for event %d has been reached", eventId));
        } else {
            request.setStatus(StateRequest.PENDING);
        }

        return RequestMapper.toParticipationRequestDto(repository.save(request));
    }

    @Transactional
    @Override
    public RequestDto cancel(Long userId, Long requestId) {
        Request request = repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format(REQUEST_NOT_FOUND, requestId)));
        if (!Objects.equals(request.getRequester().getId(), userId)) {
            throw new ForbiddenException(String.format("User %d has no access to request %d", userId, requestId));
        }
        request.setStatus(StateRequest.CANCELED);
        return RequestMapper.toParticipationRequestDto(repository.save(request));
    }
}
