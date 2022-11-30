package ru.practicum.ewm.ewmService.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.ewmService.controller.eventRequests.GetAllAdminRequest;
import ru.practicum.ewm.ewmService.controller.eventRequests.GetAllRequest;
import ru.practicum.ewm.ewmService.exception.ForbiddenException;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class EventServiceImplTest {

    private final EventService service;
    @Autowired
    private final EventRepository repository;
    @Autowired
    private final RequestRepository requestRepository;
    @MockBean
    private final StatService statService;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final CategoryRepository categoryRepository;
    private User user1;
    private User user2;
    private Category category1;
    private Category category2;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(User.builder()
                .name("user1")
                .email("user1@email.com")
                .build());
        user2 = userRepository.save(User.builder()
                .name("user2")
                .email("user2@email.com")
                .build());
        category1 = categoryRepository.save(Category.builder()
                .id(1L)
                .name("category1")
                .build());
        category2 = categoryRepository.save(Category.builder()
                .id(2L)
                .name("category2")
                .build());
    }

    @Test
    void getAllTextInAnnotation() {
        Event event1 = repository.save(createEvent(1L, category1, user1, State.PUBLISHED));
        Event event2 = repository.save(createEvent(2L, category1, user1, State.PUBLISHED));

        GetAllRequest request = GetAllRequest.builder()
                .text("annotation for event1")
                .from(0)
                .size(10)
                .build();

        Map<Long, Long> views = Map.of(event1.getId(), 0L);
        when(statService.getViewsForEvents(anyList(), any())).thenReturn(views);
        doNothing().when(statService).saveHit(any());

        List<EventShortDto> events = service.getAll(request, null);

        assertThat(events).hasSize(1)
                .contains(EventMapper.toEventShortDto(event1, 0, 0))
                .doesNotContain(EventMapper.toEventShortDto(event2, 0, 0));
    }

    @Test
    void getAllTextInDescription() {
        Event event1 = repository.save(createEvent(1L, category1, user1, State.PUBLISHED));
        Event event2 = repository.save(createEvent(2L, category1, user1, State.PUBLISHED));

        GetAllRequest request = GetAllRequest.builder()
                .text("description of event2")
                .from(0)
                .size(10)
                .build();

        Map<Long, Long> views = Map.of(event2.getId(), 0L);
        when(statService.getViewsForEvents(anyList(), any())).thenReturn(views);
        doNothing().when(statService).saveHit(any());

        List<EventShortDto> events = service.getAll(request, null);

        assertThat(events).hasSize(1)
                .contains(EventMapper.toEventShortDto(event2, 0, 0))
                .doesNotContain(EventMapper.toEventShortDto(event1, 0, 0));
    }

    @Test
    void getAllCategoryIn() {
        Event event1 = repository.save(createEvent(1L, category1, user1, State.PUBLISHED));
        Event event2 = repository.save(createEvent(2L, category1, user1, State.PUBLISHED));
        Event event3 = repository.save(createEvent(3L, category2, user2, State.PUBLISHED));

        GetAllRequest request = GetAllRequest.builder()
                .categories(Collections.singletonList(category1.getId()))
                .from(0)
                .size(10)
                .build();

        Map<Long, Long> views = Map.of(event1.getId(), 0L, event2.getId(), 0L);
        when(statService.getViewsForEvents(anyList(), any())).thenReturn(views);
        doNothing().when(statService).saveHit(any());

        List<EventShortDto> events = service.getAll(request, null);

        assertThat(events).hasSize(2)
                .contains(EventMapper.toEventShortDto(event1, 0, 0))
                .contains(EventMapper.toEventShortDto(event2, 0, 0))
                .doesNotContain(EventMapper.toEventShortDto(event3, 0, 0));
    }

    @Test
    void getAllPaid() {
        Event event1 = repository.save(createEvent(1L, category1, user1, State.PUBLISHED));
        Event event2 = repository.save(createEvent(2L, category1, user1, State.PUBLISHED));
        Event event3 = repository.save(createEvent(3L, category2, user2, State.PUBLISHED));

        event1.setPaid(false);

        GetAllRequest request = GetAllRequest.builder()
                .paid(true)
                .from(0)
                .size(10)
                .build();

        Map<Long, Long> views = Map.of(event2.getId(), 0L, event3.getId(), 0L);
        when(statService.getViewsForEvents(anyList(), any())).thenReturn(views);
        doNothing().when(statService).saveHit(any());

        List<EventShortDto> events = service.getAll(request, null);

        assertThat(events).hasSize(2)
                .contains(EventMapper.toEventShortDto(event2, 0, 0))
                .contains(EventMapper.toEventShortDto(event3, 0, 0))
                .doesNotContain(EventMapper.toEventShortDto(event1, 0, 0));
    }

    @Test
    void getAllRangeStart() {
        Event event1 = repository.save(createEvent(1L, category1, user1, State.PUBLISHED));
        Event event2 = repository.save(createEvent(2L, category1, user1, State.PUBLISHED));
        Event event3 = repository.save(createEvent(3L, category2, user2, State.PUBLISHED));

        GetAllRequest request = GetAllRequest.builder()
                .rangeStart(event1.getEventDate().plusMinutes(1))
                .from(0)
                .size(10)
                .build();

        Map<Long, Long> views = Map.of(event2.getId(), 0L, event3.getId(), 0L);
        when(statService.getViewsForEvents(anyList(), any())).thenReturn(views);
        doNothing().when(statService).saveHit(any());

        List<EventShortDto> events = service.getAll(request, null);

        assertThat(events).hasSize(2)
                .contains(EventMapper.toEventShortDto(event2, 0, 0))
                .contains(EventMapper.toEventShortDto(event3, 0, 0))
                .doesNotContain(EventMapper.toEventShortDto(event1, 0, 0));
    }

    @Test
    void getAllRangeEnd() {
        Event event1 = repository.save(createEvent(1L, category1, user1, State.PUBLISHED));
        Event event2 = repository.save(createEvent(2L, category1, user1, State.PUBLISHED));
        Event event3 = repository.save(createEvent(3L, category2, user2, State.PUBLISHED));

        GetAllRequest request = GetAllRequest.builder()
                .rangeEnd(event2.getEventDate().plusMinutes(1))
                .from(0)
                .size(10)
                .build();

        Map<Long, Long> views = Map.of(event1.getId(), 0L, event2.getId(), 0L);
        when(statService.getViewsForEvents(anyList(), any())).thenReturn(views);
        doNothing().when(statService).saveHit(any());

        List<EventShortDto> events = service.getAll(request, null);

        assertThat(events).hasSize(2)
                .contains(EventMapper.toEventShortDto(event1, 0, 0))
                .contains(EventMapper.toEventShortDto(event2, 0, 0))
                .doesNotContain(EventMapper.toEventShortDto(event3, 0, 0));
    }

    @Test
    void getAllOnlyAvailable() {
        Event event1 = repository.save(createEvent(1L, category1, user1, State.PUBLISHED));
        Event event2 = repository.save(createEvent(2L, category1, user1, State.PUBLISHED));
        Event event3 = repository.save(createEvent(3L, category2, user2, State.PUBLISHED));

        event2.setParticipantLimit(1);
        requestRepository.save(Request.builder()
                .event(event2)
                .requester(user2)
                .created(LocalDateTime.now())
                .status(StateRequest.PENDING)
                .build());

        GetAllRequest request = GetAllRequest.builder()
                .onlyAvailable(true)
                .from(0)
                .size(10)
                .build();

        Map<Long, Long> views = Map.of(event1.getId(), 0L, event3.getId(), 0L);
        when(statService.getViewsForEvents(anyList(), any())).thenReturn(views);
        doNothing().when(statService).saveHit(any());

        List<EventShortDto> events = service.getAll(request, null);

        assertThat(events).hasSize(2)
                .contains(EventMapper.toEventShortDto(event1, 0, 0))
                .contains(EventMapper.toEventShortDto(event3, 0, 0))
                .doesNotContain(EventMapper.toEventShortDto(event2, 0, 0));
    }

    @Test
    void getAllSortByDate() {
        Event event1 = repository.save(createEvent(1L, category1, user1, State.PUBLISHED));
        Event event2 = repository.save(createEvent(2L, category1, user1, State.PUBLISHED));
        Event event3 = repository.save(createEvent(3L, category2, user2, State.PUBLISHED));

        GetAllRequest request = GetAllRequest.builder()
                .sort(SortEvents.EVENT_DATE)
                .from(0)
                .size(10)
                .build();

        Map<Long, Long> views = Map.of(event1.getId(), 0L, event2.getId(), 0L, event3.getId(), 0L);
        when(statService.getViewsForEvents(anyList(), any())).thenReturn(views);
        doNothing().when(statService).saveHit(any());

        List<EventShortDto> events = service.getAll(request, null);

        assertThat(events).hasSize(3)
                .containsExactly(EventMapper.toEventShortDto(event1, 0, 0),
                        EventMapper.toEventShortDto(event2, 0, 0),
                        EventMapper.toEventShortDto(event3, 0, 0));
    }

    @Test
    void getAllSortByViews() {
        Event event1 = repository.save(createEvent(1L, category1, user1, State.PUBLISHED));
        Event event2 = repository.save(createEvent(2L, category1, user1, State.PUBLISHED));
        Event event3 = repository.save(createEvent(3L, category2, user2, State.PUBLISHED));

        GetAllRequest request = GetAllRequest.builder()
                .sort(SortEvents.VIEWS)
                .from(0)
                .size(10)
                .build();

        Map<Long, Long> views = Map.of(event1.getId(), 5L, event2.getId(), 2L, event3.getId(), 10L);
        when(statService.getViewsForEvents(anyList(), any())).thenReturn(views);
        doNothing().when(statService).saveHit(any());

        List<EventShortDto> events = service.getAll(request, null);

        assertThat(events).hasSize(3)
                .containsExactly(EventMapper.toEventShortDto(event3, 0, 10),
                        EventMapper.toEventShortDto(event1, 0, 5),
                        EventMapper.toEventShortDto(event2, 0, 2));
    }

    @Test
    void getAllSortByIds() {
        Event event1 = repository.save(createEvent(1L, category1, user1, State.PUBLISHED));
        Event event2 = repository.save(createEvent(2L, category1, user1, State.PUBLISHED));
        Event event3 = repository.save(createEvent(3L, category2, user2, State.PUBLISHED));

        GetAllRequest request = GetAllRequest.builder()
                .from(0)
                .size(10)
                .build();

        Map<Long, Long> views = Map.of(event1.getId(), 0L, event2.getId(), 0L, event3.getId(), 0L);
        when(statService.getViewsForEvents(anyList(), any())).thenReturn(views);
        doNothing().when(statService).saveHit(any());

        List<EventShortDto> events = service.getAll(request, null);

        assertThat(events).hasSize(3)
                .containsExactly(EventMapper.toEventShortDto(event3, 0, 0),
                        EventMapper.toEventShortDto(event2, 0, 0),
                        EventMapper.toEventShortDto(event1, 0, 0));
    }

    @Test
    void get() {
        Event event = repository.save(createEvent(1L, category1, user1, State.PUBLISHED));

        doNothing().when(statService).saveHit(any());
        when(statService.getViewsForEvent(any(), any())).thenReturn(0L);

        EventFullDto savedEvent = service.get(event.getId(), null);

        assertThat(savedEvent).isEqualTo(EventMapper.toEventFullDto(event, 0, 0));
    }

    @Test
    void getAllByUser() {
        Event event1 = repository.save(createEvent(1L, category1, user1, State.PUBLISHED));
        Event event2 = repository.save(createEvent(2L, category1, user1, State.PUBLISHED));
        Event event3 = repository.save(createEvent(3L, category2, user2, State.PUBLISHED));

        Map<Long, Long> views = Map.of(event1.getId(), 0L, event2.getId(), 0L);
        when(statService.getViewsForEvents(anyList(), any())).thenReturn(views);

        List<EventShortDto> events = service.getAllByUser(user1.getId(), 0, 10);

        assertThat(events).hasSize(2)
                .contains(EventMapper.toEventShortDto(event1, 0, 0))
                .contains(EventMapper.toEventShortDto(event2, 0, 0))
                .doesNotContain(EventMapper.toEventShortDto(event3, 0, 0));
    }

    @Test
    void update() {
        Event event1 = repository.save(createEvent(1L, category1, user1, State.PENDING));

        UpdateEventRequest eventForUpdate = UpdateEventRequest.builder()
                .eventId(event1.getId())
                .eventDate(event1.getEventDate())
                .annotation(event1.getAnnotation())
                .category(event1.getCategory().getId())
                .description(event1.getDescription())
                .title("updated event")
                .paid(event1.getPaid())
                .participantLimit(event1.getParticipantLimit())
                .build();

        when(statService.getViewsForEvent(any(), any())).thenReturn(0L);

        EventFullDto updatedEvent = service.update(eventForUpdate, event1.getInitiator().getId());

        assertThat(updatedEvent).hasFieldOrPropertyWithValue("id", event1.getId())
                .hasFieldOrPropertyWithValue("title", updatedEvent.getTitle());
    }

    @Test
    void save() {
        NewEventDto event = EventMapper.toNewEventDto(createEvent(1L, category1, user1, State.PENDING));

        when(statService.getViewsForEvent(any(), any())).thenReturn(0L);

        EventFullDto savedEvent = service.save(event, user1.getId());

        Optional<Event> eventFromRepo = repository.findByIdAndInitiatorId(savedEvent.getId(),
                savedEvent.getInitiator().getId());

        assertThat(eventFromRepo).isNotEmpty();
        assertThat(eventFromRepo.get()).hasFieldOrPropertyWithValue("id", savedEvent.getId())
                .hasFieldOrPropertyWithValue("title", savedEvent.getTitle())
                .hasFieldOrPropertyWithValue("state", savedEvent.getState())
                .hasFieldOrPropertyWithValue("eventDate", savedEvent.getEventDate())
                .hasFieldOrPropertyWithValue("initiator", user1);
    }

    @Test
    void getByUser() {
        Event event = repository.save(createEvent(1L, category1, user1, State.PENDING));

        when(statService.getViewsForEvent(any(), any())).thenReturn(0L);
        EventFullDto eventFullDto = service.getByUser(event.getId(), event.getInitiator().getId());

        assertThat(eventFullDto).isEqualTo(EventMapper.toEventFullDto(event, 0, 0));
    }

    @Test
    void cancel() {
        Event event1 = repository.save(createEvent(1L, category1, user1, State.PUBLISHED));

        ForbiddenException thrown = assertThrows(ForbiddenException.class, () -> {
            service.cancel(event1.getId(), event1.getInitiator().getId());
        });

        assertThat(thrown.getMessage()).isNotNull()
                .isEqualTo(event1.getState().name() + " event can't be canceled");

        Event event2 = repository.save(createEvent(2L, category1, user1, State.PENDING));
        service.cancel(event2.getId(), event2.getInitiator().getId());

        Optional<Event> event2fromRepo = repository.findByIdAndState(event2.getId(), State.CANCELED);

        assertThat(event2fromRepo).isNotEmpty();
    }

    @Test
    void getRequests() {
        Event event = repository.save(createEvent(1L, category1, user1, State.PUBLISHED));
        Request request1 = requestRepository.save(Request.builder()
                .requester(user2)
                .status(StateRequest.PENDING)
                .created(LocalDateTime.now())
                .event(event)
                .build());

        Request request2 = requestRepository.save(Request.builder()
                .requester(user2)
                .status(StateRequest.PENDING)
                .created(LocalDateTime.now())
                .event(event)
                .build());

        List<RequestDto> requestDtos = service.getRequests(event.getId(), event.getInitiator().getId());

        assertThat(requestDtos).hasSize(2)
                .contains(RequestMapper.toRequestDto(request1))
                .contains(RequestMapper.toRequestDto(request2));
    }

    @Test
    void confirmRequest() {
        Event event = repository.save(createEvent(1L, category1, user1, State.PUBLISHED));
        event.setParticipantLimit(1);
        Request request1 = requestRepository.save(Request.builder()
                .requester(user2)
                .status(StateRequest.PENDING)
                .created(LocalDateTime.now())
                .event(event)
                .build());

        RequestDto confirmedRequest = service.confirmRequest(event.getId(), event.getInitiator().getId(),
                request1.getId());

        assertThat(confirmedRequest).hasFieldOrPropertyWithValue("status", StateRequest.CONFIRMED)
                .hasFieldOrPropertyWithValue("id", request1.getId());

        Request request2 = requestRepository.save(Request.builder()
                .requester(user2)
                .status(StateRequest.PENDING)
                .created(LocalDateTime.now())
                .event(event)
                .build());

        ForbiddenException thrown = assertThrows(ForbiddenException.class, () -> {
            service.confirmRequest(event.getId(), event.getInitiator().getId(), request2.getId());
        });

        assertThat(thrown.getMessage()).isNotNull()
                .isEqualTo(String.format("Participants limit for event %d has been reached", event.getId()));
    }

    @Test
    void rejectRequests() {
        Event event = repository.save(createEvent(1L, category1, user1, State.PUBLISHED));
        Request request = requestRepository.save(Request.builder()
                .requester(user2)
                .status(StateRequest.PENDING)
                .created(LocalDateTime.now())
                .event(event)
                .build());

        RequestDto rejectedRequest = service.rejectRequest(event.getId(), event.getInitiator().getId(),
                request.getId());

        assertThat(rejectedRequest).hasFieldOrPropertyWithValue("status", StateRequest.REJECTED)
                .hasFieldOrPropertyWithValue("id", request.getId());
    }

    @Test
    void getAllAdminUserIdIn() {
        Event event1 = repository.save(createEvent(1L, category1, user1, State.PUBLISHED));
        Event event2 = repository.save(createEvent(2L, category1, user1, State.PUBLISHED));
        Event event3 = repository.save(createEvent(3L, category2, user2, State.PUBLISHED));

        GetAllAdminRequest request = GetAllAdminRequest.builder()
                .users(Collections.singletonList(event1.getInitiator().getId()))
                .from(0)
                .size(10)
                .build();

        Map<Long, Long> views = Map.of(event1.getId(), 0L, event2.getId(), 0L);
        when(statService.getViewsForEvents(anyList(), any())).thenReturn(views);

        List<EventFullDto> events = service.getAllAdmin(request);

        assertThat(events).hasSize(2)
                .contains(EventMapper.toEventFullDto(event1, 0, 0))
                .contains(EventMapper.toEventFullDto(event2, 0, 0))
                .doesNotContain(EventMapper.toEventFullDto(event3, 0, 0));
    }

    @Test
    void getAllAdminCategoryIn() {
        Event event1 = repository.save(createEvent(1L, category1, user1, State.PUBLISHED));
        Event event2 = repository.save(createEvent(2L, category1, user1, State.PUBLISHED));
        Event event3 = repository.save(createEvent(3L, category2, user2, State.PUBLISHED));

        GetAllAdminRequest request = GetAllAdminRequest.builder()
                .users(Collections.singletonList(event1.getInitiator().getId()))
                .from(0)
                .size(10)
                .build();

        Map<Long, Long> views = Map.of(event1.getId(), 0L, event2.getId(), 0L);
        when(statService.getViewsForEvents(anyList(), any())).thenReturn(views);

        List<EventFullDto> events = service.getAllAdmin(request);

        assertThat(events).hasSize(2)
                .contains(EventMapper.toEventFullDto(event1, 0, 0))
                .contains(EventMapper.toEventFullDto(event2, 0, 0))
                .doesNotContain(EventMapper.toEventFullDto(event3, 0, 0));
    }

    @Test
    void getAllAdminStateIn() {
        Event event1 = repository.save(createEvent(1L, category1, user1, State.PUBLISHED));
        Event event2 = repository.save(createEvent(2L, category1, user1, State.PUBLISHED));
        Event event3 = repository.save(createEvent(3L, category2, user2, State.PUBLISHED));

        GetAllAdminRequest request = GetAllAdminRequest.builder()
                .states(Collections.singletonList(State.PUBLISHED))
                .from(0)
                .size(10)
                .build();

        Map<Long, Long> views = Map.of(event1.getId(), 0L, event2.getId(), 0L, event3.getId(), 0L);
        when(statService.getViewsForEvents(anyList(), any())).thenReturn(views);
        doNothing().when(statService).saveHit(any());

        List<EventFullDto> events = service.getAllAdmin(request);

        assertThat(events).hasSize(3)
                .contains(EventMapper.toEventFullDto(event1, 0, 0))
                .contains(EventMapper.toEventFullDto(event2, 0, 0))
                .contains(EventMapper.toEventFullDto(event3, 0, 0));
    }

    @Test
    void getAllAdminAfterRangeStart() {
        Event event1 = repository.save(createEvent(1L, category1, user1, State.PUBLISHED));
        Event event2 = repository.save(createEvent(2L, category1, user1, State.PUBLISHED));
        Event event3 = repository.save(createEvent(3L, category2, user2, State.PUBLISHED));

        GetAllAdminRequest request = GetAllAdminRequest.builder()
                .rangeStart(event1.getEventDate().plusMinutes(1))
                .from(0)
                .size(10)
                .build();

        Map<Long, Long> views = Map.of(event2.getId(), 0L, event3.getId(), 0L);
        when(statService.getViewsForEvents(anyList(), any())).thenReturn(views);
        doNothing().when(statService).saveHit(any());

        List<EventFullDto> events = service.getAllAdmin(request);

        assertThat(events).hasSize(2)
                .contains(EventMapper.toEventFullDto(event2, 0, 0))
                .contains(EventMapper.toEventFullDto(event3, 0, 0))
                .doesNotContain(EventMapper.toEventFullDto(event1, 0, 0));
    }

    @Test
    void getAllAdminAfterRangeEnd() {
        Event event1 = repository.save(createEvent(1L, category1, user1, State.PUBLISHED));
        Event event2 = repository.save(createEvent(2L, category1, user1, State.PUBLISHED));
        Event event3 = repository.save(createEvent(3L, category2, user2, State.PUBLISHED));

        GetAllAdminRequest request = GetAllAdminRequest.builder()
                .rangeEnd(event3.getEventDate().minusMinutes(1))
                .from(0)
                .size(10)
                .build();

        Map<Long, Long> views = Map.of(event1.getId(), 0L, event2.getId(), 0L);
        when(statService.getViewsForEvents(anyList(), any())).thenReturn(views);
        doNothing().when(statService).saveHit(any());

        List<EventFullDto> events = service.getAllAdmin(request);

        assertThat(events).hasSize(2)
                .contains(EventMapper.toEventFullDto(event1, 0, 0))
                .contains(EventMapper.toEventFullDto(event2, 0, 0))
                .doesNotContain(EventMapper.toEventFullDto(event3, 0, 0));
    }

    @Test
    void getAllAdminOrder() {
        Event event1 = repository.save(createEvent(1L, category1, user1, State.PUBLISHED));
        Event event2 = repository.save(createEvent(2L, category1, user1, State.PUBLISHED));
        Event event3 = repository.save(createEvent(3L, category2, user2, State.PUBLISHED));

        GetAllAdminRequest request = GetAllAdminRequest.builder()
                .from(0)
                .size(10)
                .build();

        Map<Long, Long> views = Map.of(event1.getId(), 0L, event2.getId(), 0L, event3.getId(), 0L);
        when(statService.getViewsForEvents(anyList(), any())).thenReturn(views);
        doNothing().when(statService).saveHit(any());

        List<EventFullDto> events = service.getAllAdmin(request);

        assertThat(events).hasSize(3)
                .containsExactly(EventMapper.toEventFullDto(event3, 0, 0),
                        EventMapper.toEventFullDto(event2, 0, 0),
                        EventMapper.toEventFullDto(event1, 0, 0));
    }

    @Test
    void editEvent() {
        Event event = repository.save(createEvent(1L, category1, user1, State.PUBLISHED));
        NewEventDto eventForUpdate = EventMapper.toNewEventDto(event);
        eventForUpdate.setAnnotation("Edited annotation for event1");

        EventFullDto editedEvent = service.editEvent(eventForUpdate, event.getId());

        assertThat(editedEvent).hasFieldOrPropertyWithValue("id", event.getId())
                .hasFieldOrPropertyWithValue("annotation", eventForUpdate.getAnnotation());
    }

    @Test
    void publishEvent() {
        Event event1 = repository.save(createEvent(1L, category1, user1, State.PENDING));
        EventFullDto publishedEvent = service.publishEvent(event1.getId());

        assertThat(publishedEvent).hasFieldOrPropertyWithValue("state", State.PUBLISHED);

        Event event2 = repository.save(createEvent(2L, category1, user1, State.CANCELED));

        ForbiddenException stateChange = assertThrows(ForbiddenException.class, () -> {
            service.publishEvent(event2.getId());
        });

        assertThat(stateChange.getMessage()).isNotNull()
                .isEqualTo("Event status can't be changed");

        Event event3 = repository.save(createEvent(3L, category1, user1, State.PENDING));
        event3.setEventDate(LocalDateTime.now().plusHours(1).minusMinutes(1));

        ForbiddenException wrongDate = assertThrows(ForbiddenException.class, () -> {
            service.publishEvent(event3.getId());
        });

        assertThat(wrongDate.getMessage()).isNotNull()
                .isEqualTo("Event date must be at least one hour after the publication");
    }

    @Test
    void rejectEvent() {
        Event event1 = repository.save(createEvent(1L, category1, user1, State.PENDING));
        EventFullDto canceledEvent = service.rejectEvent(event1.getId());

        assertThat(canceledEvent).hasFieldOrPropertyWithValue("state", State.CANCELED);

        Event event2 = repository.save(createEvent(2L, category1, user1, State.PUBLISHED));

        ForbiddenException stateChange = assertThrows(ForbiddenException.class, () -> {
            service.rejectEvent(event2.getId());
        });

        assertThat(stateChange.getMessage()).isNotNull()
                .isEqualTo("Event status can't be changed");
    }

    @Test
    void convertToListEventShortDto() {
        Event event1 = repository.save(createEvent(1L, category1, user1, State.PUBLISHED));
        Event event2 = repository.save(createEvent(2L, category1, user1, State.PUBLISHED));
        Event event3 = repository.save(createEvent(3L, category2, user2, State.PUBLISHED));

        Map<Long, Long> views = Map.of(event1.getId(), 0L, event2.getId(), 0L, event3.getId(), 0L);
        when(statService.getViewsForEvents(anyList(), any())).thenReturn(views);

        List<EventShortDto> events = service.convertToListEventShortDto(List.of(event1, event2, event3), false);

        assertThat(events).hasSize(3)
                .containsExactly(EventMapper.toEventShortDto(event1, 0, 0),
                        EventMapper.toEventShortDto(event2, 0, 0),
                        EventMapper.toEventShortDto(event3, 0, 0));
    }

    private Event createEvent(Long eventId, Category category, User user, State state) {
        return Event.builder()
                .annotation(String.format("annotation for event%d", eventId))
                .title(String.format("title of event%d", eventId))
                .category(category)
                .description(String.format("description of event%d", eventId))
                .eventDate(LocalDateTime.now().plusHours(eventId + 2))
                .location(new Location(52.41621224781127, 13.596652621929262))
                .paid(true)
                .participantLimit(0)
                .requestModeration(false)
                .initiator(user)
                .state(state)
                .build();
    }
}