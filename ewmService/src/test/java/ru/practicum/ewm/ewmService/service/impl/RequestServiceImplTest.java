package ru.practicum.ewm.ewmService.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.ewmService.exception.AlreadyExistsException;
import ru.practicum.ewm.ewmService.exception.ForbiddenException;
import ru.practicum.ewm.ewmService.mapper.RequestMapper;
import ru.practicum.ewm.ewmService.model.category.Category;
import ru.practicum.ewm.ewmService.model.event.Event;
import ru.practicum.ewm.ewmService.model.event.Location;
import ru.practicum.ewm.ewmService.model.event.State;
import ru.practicum.ewm.ewmService.model.request.Request;
import ru.practicum.ewm.ewmService.model.request.RequestDto;
import ru.practicum.ewm.ewmService.model.request.StateRequest;
import ru.practicum.ewm.ewmService.model.user.User;
import ru.practicum.ewm.ewmService.repository.CategoryRepository;
import ru.practicum.ewm.ewmService.repository.EventRepository;
import ru.practicum.ewm.ewmService.repository.RequestRepository;
import ru.practicum.ewm.ewmService.repository.UserRepository;
import ru.practicum.ewm.ewmService.service.RequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestServiceImplTest {

    private final RequestService service;
    @Autowired
    private final RequestRepository repository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final CategoryRepository categoryRepository;
    private User user;
    private User requester;
    private User requester2;
    private Category category;
    private Event event;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .name("user")
                .email("user@email.com")
                .build());

        requester = userRepository.save(User.builder()
                .name("requester")
                .email("requester@email.com")
                .build());

        requester2 = userRepository.save(User.builder()
                .name("requester2")
                .email("requester2@email.com")
                .build());

        category = categoryRepository.save(Category.builder()
                .id(1L)
                .name("category1")
                .build());

        event = eventRepository.save(Event.builder()
                .annotation("annotation for event")
                .title("title of event")
                .category(category)
                .description("description of event")
                .eventDate(LocalDateTime.now().plusDays(1))
                .location(new Location(52.41621224781127, 13.596652621929262))
                .paid(true)
                .participantLimit(0)
                .requestModeration(false)
                .initiator(user)
                .state(State.PUBLISHED)
                .build());
    }

    @Test
    void getAll() {
        Request request1 = repository.save(Request.builder()
                .event(event)
                .created(LocalDateTime.now())
                .status(StateRequest.PENDING)
                .requester(requester)
                .build());

        Request request2 = repository.save(Request.builder()
                .event(event)
                .created(LocalDateTime.now())
                .status(StateRequest.PENDING)
                .requester(requester)
                .build());

        Request request3 = repository.save(Request.builder()
                .event(event)
                .created(LocalDateTime.now())
                .status(StateRequest.PENDING)
                .requester(requester2)
                .build());

        List<RequestDto> requests = service.getAll(requester.getId());

        assertThat(requests).hasSize(2)
                .contains(RequestMapper.toRequestDto(request1))
                .contains(RequestMapper.toRequestDto(request2))
                .doesNotContain(RequestMapper.toRequestDto(request3));
    }

    @Test
    void saveStatePending() {
        event.setRequestModeration(true);
        RequestDto request = service.save(requester.getId(), event.getId());

        Optional<Request> requestFromRepo = repository.findById(request.getId());

        assertThat(requestFromRepo).isNotEmpty();
        assertThat(requestFromRepo.get()).hasFieldOrPropertyWithValue("requester", requester)
                .hasFieldOrPropertyWithValue("event", event)
                .hasFieldOrPropertyWithValue("status", StateRequest.PENDING);

    }

    @Test
    void saveStateConfirmed() {
        event.setRequestModeration(false);
        RequestDto request = service.save(requester.getId(), event.getId());

        Optional<Request> requestFromRepo = repository.findById(request.getId());

        assertThat(requestFromRepo).isNotEmpty();
        assertThat(requestFromRepo.get()).hasFieldOrPropertyWithValue("requester", requester)
                .hasFieldOrPropertyWithValue("event", event)
                .hasFieldOrPropertyWithValue("status", StateRequest.CONFIRMED);

    }

    @Test
    void saveAlreadyExist() {
        RequestDto request = service.save(requester.getId(), event.getId());

        AlreadyExistsException alreadyRequested = assertThrows(AlreadyExistsException.class, () -> {
            service.save(request.getRequester(), request.getEvent());
        });

        assertThat(alreadyRequested.getMessage()).isNotNull()
                .isEqualTo(String.format("User %d has already sent a request for event %d",
                        request.getRequester(), request.getEvent()));
    }

    @Test
    void saveOnlyInvited() {
        event.setOnlyInvited(true);

        ForbiddenException onlyInvited = assertThrows(ForbiddenException.class, () -> {
            service.save(requester.getId(), event.getId());
        });

        assertThat(onlyInvited.getMessage()).isNotNull()
                .isEqualTo(String.format("Registration for the event %d is available by invitations only",
                        event.getId()));
    }

    @Test
    void saveLimitReached() {
        event.setParticipantLimit(1);
        event.setRequestModeration(false);
        repository.save(Request.builder()
                .requester(requester)
                .event(event)
                .status(StateRequest.CONFIRMED)
                .created(LocalDateTime.now())
                .build());

        ForbiddenException alreadyRequested = assertThrows(ForbiddenException.class, () -> {
            service.save(requester2.getId(), event.getId());
        });

        assertThat(alreadyRequested.getMessage()).isNotNull()
                .isEqualTo(String.format("Participants limit for event %d has been reached", event.getId()));
    }

    @Test
    void cancel() {
        Request request = repository.save(Request.builder()
                .event(event)
                .created(LocalDateTime.now())
                .status(StateRequest.PENDING)
                .requester(requester)
                .build());

        service.cancel(request.getRequester().getId(), request.getId());

        Optional<Request> requestFromRepo = repository.findByIdAndRequesterId(request.getId(),
                request.getRequester().getId());

        assertThat(requestFromRepo).isNotEmpty();
        assertThat(requestFromRepo.get()).hasFieldOrPropertyWithValue("status", StateRequest.CANCELED);
    }
}