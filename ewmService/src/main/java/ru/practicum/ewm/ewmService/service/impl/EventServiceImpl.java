package ru.practicum.ewm.ewmService.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.ewm.ewmService.controller.eventRequests.GetAllAdminRequest;
import ru.practicum.ewm.ewmService.controller.eventRequests.GetAllRequest;
import ru.practicum.ewm.ewmService.exception.ForbiddenException;
import ru.practicum.ewm.ewmService.exception.NotFoundException;
import ru.practicum.ewm.ewmService.mapper.EventMapper;
import ru.practicum.ewm.ewmService.mapper.RequestMapper;
import ru.practicum.ewm.ewmService.model.category.Category;
import ru.practicum.ewm.ewmService.model.event.*;
import ru.practicum.ewm.ewmService.model.request.Request;
import ru.practicum.ewm.ewmService.model.request.RequestDto;
import ru.practicum.ewm.ewmService.model.request.StateRequest;
import ru.practicum.ewm.ewmService.model.user.User;
import ru.practicum.ewm.ewmService.repository.CategoryRepository;
import ru.practicum.ewm.ewmService.repository.EventRepository;
import ru.practicum.ewm.ewmService.repository.RequestRepository;
import ru.practicum.ewm.ewmService.repository.UserRepository;
import ru.practicum.ewm.ewmService.service.EventService;
import ru.practicum.ewm.ewmService.service.StatService;
import ru.practicum.ewm.ewmService.utility.Page;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.ewm.ewmService.utility.Constants.*;

@Transactional(readOnly = true)
@Service
public class EventServiceImpl implements EventService {

    private final EventRepository repository;
    private final RequestRepository requestRepository;
    private final StatService statService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public EventServiceImpl(EventRepository repository, RequestRepository requestRepository,
                            StatService statService, UserRepository userRepository,
                            CategoryRepository categoryRepository) {
        this.repository = repository;
        this.requestRepository = requestRepository;
        this.statService = statService;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<EventShortDto> getAll(GetAllRequest getAllEventsRequest, HttpServletRequest request) {
        statService.saveHit(request);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> cq = cb.createQuery(Event.class);
        Root<Event> event = cq.from(Event.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(event.get("state"), State.PUBLISHED));

        if (getAllEventsRequest.getText() != null && !getAllEventsRequest.getText().isEmpty()) {
            predicates.add(cb.or(cb.like(cb.lower(event.get("annotation")), getAllEventsRequest.getText().toLowerCase()),
                    cb.like(cb.lower(event.get("description")), getAllEventsRequest.getText().toLowerCase())));
        }
        if (!CollectionUtils.isEmpty(getAllEventsRequest.getCategories())) {
            predicates.add(event.get("category").get("id").in(getAllEventsRequest.getCategories()));
        }
        if (getAllEventsRequest.getPaid() != null) {
            predicates.add(cb.equal(event.get("paid"), getAllEventsRequest.getPaid()));
        }
        if (getAllEventsRequest.getRangeStart() != null) {
            predicates.add(cb.greaterThan(event.get("eventDate"), getAllEventsRequest.getRangeStart()));
        }
        if (getAllEventsRequest.getRangeEnd() != null) {
            predicates.add(cb.lessThan(event.get("eventDate"), getAllEventsRequest.getRangeEnd()));
        }
        if (getAllEventsRequest.getOnlyAvailable() != null && getAllEventsRequest.getOnlyAvailable()) {
            predicates.add(cb.or(cb.equal(event.get("participantLimit"), 0), getRequestsPredicate(cb, cq, event)));
        }
        if (getAllEventsRequest.getSort() == null) {
            cq.orderBy(cb.desc(event.get("id")));
        }
        if (getAllEventsRequest.getSort() == SortEvents.EVENT_DATE) {
            cq.orderBy(cb.asc(event.get("eventDate")));
        }

        cq.select(event).where(predicates.toArray(new Predicate[]{}));

        List<Event> events = entityManager.createQuery(cq)
                .setMaxResults(getAllEventsRequest.getSize())
                .setFirstResult(getAllEventsRequest.getFrom())
                .getResultList();

        List<EventShortDto> eventShortDto = convertToListEventShortDto(events, false);

        if (getAllEventsRequest.getSort() == SortEvents.VIEWS) {
            return eventShortDto.stream()
                    .sorted(Comparator.comparingLong(EventShortDto::getViews).reversed())
                    .collect(Collectors.toList());
        }
        return eventShortDto;
    }

    @Override
    public EventFullDto get(Long id, HttpServletRequest request) {
        statService.saveHit(request);
        Event event = repository.findByIdAndState(id, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(String.format(EVENT_NOT_FOUND, id)));
        return EventMapper.toEventFullDto(event, requestRepository.countAllByStatusAndEventId(StateRequest.CONFIRMED, id),
                statService.getViewsForEvent(event, false));
    }

    @Override
    public List<EventShortDto> getAllByUser(Long id, Integer from, Integer size) {
        return convertToListEventShortDto(repository.findAllByInitiatorId(id, Page.of(from, size)), false);
    }

    @Transactional
    @Override
    public EventFullDto update(UpdateEventRequest updateEventRequest, Long userId) {
        Event event = repository.findByIdAndInitiatorId(updateEventRequest.getEventId(), userId)
                .orElseThrow(() -> new NotFoundException(String.format(EVENT_NOT_FOUND_BY_USER,
                        updateEventRequest.getEventId(), userId)));
        if (event.getState() == State.PUBLISHED) {
            throw new ForbiddenException("Only pending or canceled events can be changed");
        }
        if (event.getState() == State.CANCELED) {
            event.setState(State.PENDING);
        }
        Event updatedEvent = updateEventFields(event, updateEventRequest);
        return convertToEventFullDto(repository.save(updatedEvent));
    }

    @Transactional
    @Override
    public EventFullDto save(NewEventDto newEventDto, Long userId) {
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND, userId)));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException(String.format(CATEGORY_NOT_FOUND, newEventDto.getCategory())));
        Event event = EventMapper.toEvent(newEventDto, initiator, category);
        event.setState(State.PENDING);
        return convertToEventFullDto(repository.save(event));
    }

    @Override
    public EventFullDto getByUser(Long id, Long userId) {
        Event event = repository.findByIdAndInitiatorId(id, userId)
                .orElseThrow(() -> new NotFoundException(String.format(EVENT_NOT_FOUND_BY_USER, id, userId)));
        return convertToEventFullDto(event);
    }

    @Transactional
    @Override
    public EventFullDto cancel(Long id, Long userId) {
        Event event = repository.findByIdAndInitiatorId(id, userId)
                .orElseThrow(() -> new NotFoundException(String.format(EVENT_NOT_FOUND_BY_USER, id, userId)));
        if (event.getState() != State.PENDING) {
            throw new ForbiddenException(event.getState().name() + " event can't be canceled");
        }
        event.setState(State.CANCELED);
        event = repository.save(event);
        return convertToEventFullDto(event);
    }

    @Override
    public List<RequestDto> getRequests(Long id, Long userId) {
        Event event = repository.findByIdAndInitiatorId(id, userId)
                .orElseThrow(() -> new NotFoundException(String.format(EVENT_NOT_FOUND_BY_USER, id, userId)));
        return requestRepository.findAllByEventId(event.getId()).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public RequestDto confirmRequest(Long id, Long userId, Long reqId) {
        Request request = requestRepository.findByIdAndEventIdAndEventInitiatorId(reqId, id, userId)
                .orElseThrow(() -> new NotFoundException(String.format(REQUEST_NOT_FOUND +
                        " for event %d created by user %d", reqId, id, userId)));
        Event event = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(EVENT_NOT_FOUND_BY_USER, id, userId)));

        if (limitReached(event)) {
            throw new ForbiddenException(String.format("Participants limit for event %d has been reached", id));
        }

        request.setStatus(StateRequest.CONFIRMED);
        requestRepository.save(request);

        if (limitReached(event)) {
            List<Request> pendingRequests = requestRepository.findAllByEventIdAndStatus(event.getId(), StateRequest.PENDING);
            for (Request r : pendingRequests) {
                r.setStatus(StateRequest.REJECTED);
                requestRepository.save(r);
            }
        }
        return RequestMapper.toRequestDto(request);
    }

    @Transactional
    @Override
    public RequestDto rejectRequest(Long id, Long userId, Long reqId) {
        Request request = requestRepository.findByIdAndEventIdAndEventInitiatorId(reqId, id, userId)
                .orElseThrow(() -> new NotFoundException(String.format(REQUEST_NOT_FOUND +
                        " for event %d created by user %d", reqId, id, userId)));
        request.setStatus(StateRequest.REJECTED);
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public List<EventFullDto> getAllAdmin(GetAllAdminRequest getAllAdminRequest) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> cq = cb.createQuery(Event.class);
        Root<Event> event = cq.from(Event.class);
        List<Predicate> predicates = new ArrayList<>();

        if (!CollectionUtils.isEmpty(getAllAdminRequest.getUsers())) {
            predicates.add(event.get("initiator").get("id").in(getAllAdminRequest.getUsers()));
        }
        if (!CollectionUtils.isEmpty(getAllAdminRequest.getCategories())) {
            predicates.add(event.get("category").get("id").in(getAllAdminRequest.getCategories()));
        }
        if (!CollectionUtils.isEmpty(getAllAdminRequest.getStates())) {
            predicates.add(event.get("state").in(getAllAdminRequest.getStates()));
        }
        if (getAllAdminRequest.getRangeStart() != null) {
            predicates.add(cb.greaterThan(event.get("eventDate"), getAllAdminRequest.getRangeStart()));
        }
        if (getAllAdminRequest.getRangeEnd() != null) {
            predicates.add(cb.lessThan(event.get("eventDate"), getAllAdminRequest.getRangeEnd()));
        }

        cq.select(event).where(predicates.toArray(new Predicate[]{})).orderBy(cb.desc(event.get("id")));

        List<Event> events = entityManager.createQuery(cq)
                .setMaxResults(getAllAdminRequest.getSize())
                .setFirstResult(getAllAdminRequest.getFrom())
                .getResultList();
        return convertToListEventFullDto(events, false);
    }

    @Transactional
    @Override
    public EventFullDto editEvent(NewEventDto newEventDto, Long id) {
        Event event = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(EVENT_NOT_FOUND, id)));
        Event updatedEvent = updateEventFieldsAdmin(event, newEventDto);
        return convertToEventFullDto(repository.save(updatedEvent));
    }

    @Transactional
    @Override
    public EventFullDto publishEvent(Long id) {
        Event event = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(EVENT_NOT_FOUND, id)));
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ForbiddenException("Event date must be at least one hour after the publication");
        }
        if (event.getState() != State.PENDING) {
            throw new ForbiddenException("Event status can't be changed");
        }
        event.setState(State.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());
        return convertToEventFullDto(repository.save(event));
    }

    @Transactional
    @Override
    public EventFullDto rejectEvent(Long id) {
        Event event = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(EVENT_NOT_FOUND, id)));
        if (event.getState() == State.PUBLISHED) {
            throw new ForbiddenException("Event status can't be changed");
        }
        event.setState(State.CANCELED);
        return convertToEventFullDto(repository.save(event));
    }

    @Override
    public List<EventShortDto> convertToListEventShortDto(List<Event> events, Boolean uniqueRequests) {
        Map<Long, Long> confirmedRequests = getConfirmedRequests(events);
        Map<Long, Long> views = statService.getViewsForEvents(events, uniqueRequests);
        return events.stream()
                .map(event -> EventMapper.toEventShortDto(event,
                        confirmedRequests.get(event.getId()),
                        views.get(event.getId())))
                .collect(Collectors.toList());
    }

    private List<EventFullDto> convertToListEventFullDto(List<Event> events, Boolean uniqueRequests) {
        Map<Long, Long> confirmedRequests = getConfirmedRequests(events);
        Map<Long, Long> views = statService.getViewsForEvents(events, uniqueRequests);
        return events.stream()
                .map(event -> EventMapper.toEventFullDto(event,
                        confirmedRequests.get(event.getId()),
                        views.get(event.getId())))
                .collect(Collectors.toList());
    }

    private EventFullDto convertToEventFullDto(Event event) {
        return EventMapper.toEventFullDto(event,
                requestRepository.countAllByStatusAndEventId(StateRequest.CONFIRMED, event.getId()),
                statService.getViewsForEvent(event, false));
    }

    private boolean limitReached(Event event) {
        int participantLimit = event.getParticipantLimit();
        long confirmedRequests = requestRepository.countAllByStatusAndEventId(StateRequest.CONFIRMED, event.getId());
        return participantLimit != 0 && participantLimit == confirmedRequests;
    }

    private Event updateEventFields(Event event, @NotNull UpdateEventRequest updateEventRequest) {
        String annotation = updateEventRequest.getAnnotation();
        String title = updateEventRequest.getTitle();
        Long categoryId = updateEventRequest.getCategory();
        String description = updateEventRequest.getDescription();
        LocalDateTime eventDate = updateEventRequest.getEventDate();
        Boolean paid = updateEventRequest.getPaid();
        Integer participantLimit = updateEventRequest.getParticipantLimit();

        if (annotation != null) {
            event.setAnnotation(annotation);
        }
        if (title != null) {
            event.setTitle(title);
        }
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException(String.format(CATEGORY_NOT_FOUND, categoryId)));
            event.setCategory(category);
        }
        if (description != null) {
            event.setDescription(description);
        }
        if (eventDate != null) {
            event.setEventDate(eventDate);
        }
        if (paid != null) {
            event.setPaid(paid);
        }
        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
        }
        return event;
    }

    private Event updateEventFieldsAdmin(Event event, @NotNull NewEventDto eventDto) {
        String annotation = eventDto.getAnnotation();
        String title = eventDto.getTitle();
        Long categoryId = eventDto.getCategory();
        String description = eventDto.getDescription();
        LocalDateTime eventDate = eventDto.getEventDate();
        Boolean paid = eventDto.getPaid();
        Integer participantLimit = eventDto.getParticipantLimit();
        Boolean requestModeration = eventDto.getRequestModeration();

        if (annotation != null) {
            event.setAnnotation(annotation);
        }
        if (title != null) {
            event.setTitle(title);
        }
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException(String.format(CATEGORY_NOT_FOUND, categoryId)));
            event.setCategory(category);
        }
        if (description != null) {
            event.setDescription(description);
        }
        if (eventDate != null) {
            event.setEventDate(eventDate);
        }
        if (paid != null) {
            event.setPaid(paid);
        }
        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
        }
        if (requestModeration != null) {
            event.setRequestModeration(requestModeration);
        }
        return event;
    }

    private Map<Long, Long> getConfirmedRequests(List<Event> events) {
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        Map<Long, Long> confirmedRequests = new HashMap<>();
        for (Long[] entry : repository.countAllByStatusAndEventIdIn(StateRequest.CONFIRMED.name(), eventIds)) {
            confirmedRequests.put(entry[0], entry[1]);
        }
        return confirmedRequests;
    }

    private Predicate getRequestsPredicate(CriteriaBuilder cb, CriteriaQuery<Event> cq, Root<Event> event) {
        Subquery<Long> subQuery = cq.subquery(Long.class);
        Root<Request> requestRoot = subQuery.from(Request.class);
        Join<Request, Event> eventsRequests = requestRoot.join("event");
        return cb.lessThan(event.get("participantLimit"),
                subQuery.select(cb.count(requestRoot.get("id")))
                        .where(cb.equal(eventsRequests.get("id"), requestRoot.get("event").get("id")))
                        .where(cb.equal(requestRoot.get("status"), StateRequest.CONFIRMED)));
    }
}
