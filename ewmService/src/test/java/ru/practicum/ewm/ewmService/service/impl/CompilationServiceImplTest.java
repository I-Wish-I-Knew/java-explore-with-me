package ru.practicum.ewm.ewmService.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.ewmService.mapper.CompilationMapper;
import ru.practicum.ewm.ewmService.mapper.EventMapper;
import ru.practicum.ewm.ewmService.model.category.Category;
import ru.practicum.ewm.ewmService.model.compilation.Compilation;
import ru.practicum.ewm.ewmService.model.compilation.CompilationDto;
import ru.practicum.ewm.ewmService.model.event.Event;
import ru.practicum.ewm.ewmService.model.event.EventShortDto;
import ru.practicum.ewm.ewmService.model.event.Location;
import ru.practicum.ewm.ewmService.model.event.State;
import ru.practicum.ewm.ewmService.model.user.User;
import ru.practicum.ewm.ewmService.repository.CategoryRepository;
import ru.practicum.ewm.ewmService.repository.CompilationRepository;
import ru.practicum.ewm.ewmService.repository.EventRepository;
import ru.practicum.ewm.ewmService.repository.UserRepository;
import ru.practicum.ewm.ewmService.service.CompilationService;
import ru.practicum.ewm.ewmService.service.StatService;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CompilationServiceImplTest {

    private final CompilationService service;
    @Autowired
    private final CompilationRepository repository;
    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final CategoryRepository categoryRepository;
    @MockBean
    private final StatService statService;
    private Event event1;
    private Event event2;
    private Event event3;

    @BeforeEach
    void setUp() {
        User user = userRepository.save(User.builder()
                .email("user@email.com")
                .name("user")
                .build());

        Category category = categoryRepository.save(Category.builder().name("category").build());

        event1 = eventRepository.save(Event.builder()
                .annotation("annotation for event1")
                .title("title of event1")
                .category(category)
                .description("description of event1")
                .eventDate(LocalDateTime.now().plusHours(10))
                .location(new Location(52.41621224781127, 13.596652621929262))
                .paid(true)
                .participantLimit(0)
                .requestModeration(false)
                .initiator(user)
                .state(State.PUBLISHED)
                .build());

        event2 = eventRepository.save(Event.builder()
                .annotation("annotation for event2")
                .title("title of event2")
                .category(category)
                .description("description of event2")
                .eventDate(LocalDateTime.now().plusHours(12))
                .location(new Location(52.41621224781127, 13.596652621929262))
                .paid(true)
                .participantLimit(0)
                .requestModeration(false)
                .initiator(user)
                .state(State.PUBLISHED)
                .build());

        event3 = eventRepository.save(Event.builder()
                .annotation("annotation for event3")
                .title("title of event3")
                .category(category)
                .description("description of event3")
                .eventDate(LocalDateTime.now().plusHours(8))
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
        Compilation compilation = repository.save(Compilation.builder()
                .events(new HashSet<>(Arrays.asList(event2, event3)))
                .title("Compilation")
                .pinned(false)
                .build());

        Map<Long, Long> views = Map.of(event2.getId(), 0L, event3.getId(), 0L);
        when(statService.getViewsForEvents(anyList(), any())).thenReturn(views);

        List<CompilationDto> compilations = service.getAll(false, 0, 10);
        List<EventShortDto> eventsCompilation1 = Arrays.asList(EventMapper.toEventShortDto(event2, 0, 0),
                EventMapper.toEventShortDto(event3, 0, 0));

        assertThat(compilations).hasSize(1)
                .contains(CompilationMapper.toCompilationDto(compilation, eventsCompilation1));

        compilations = service.getAll(true, 0, 10);

        assertThat(compilations).isEmpty();
    }

    @Test
    void get() {
        Compilation compilation = repository.save(Compilation.builder()
                .events(new HashSet<>(Arrays.asList(event1, event2)))
                .title("Compilation")
                .pinned(false)
                .build());

        Map<Long, Long> views = Map.of(event1.getId(), 0L, event2.getId(), 0L);
        when(statService.getViewsForEvents(anyList(), any())).thenReturn(views);

        List<EventShortDto> eventsCompilation = Arrays.asList(EventMapper.toEventShortDto(event1, 0, 0),
                EventMapper.toEventShortDto(event2, 0, 0));

        CompilationDto compilationDto = service.get(compilation.getId());

        assertThat(compilationDto).isEqualTo(CompilationMapper.toCompilationDto(compilation, eventsCompilation));
    }

    @Test
    void save() {
        Compilation compilation = Compilation.builder()
                .events(new HashSet<>(Arrays.asList(event1, event2)))
                .title("Compilation")
                .pinned(false)
                .build();

        Map<Long, Long> views = Map.of(event1.getId(), 0L, event2.getId(), 0L);
        when(statService.getViewsForEvents(anyList(), any())).thenReturn(views);

        CompilationDto compilationDto = service.save(CompilationMapper.toNewCompilationDto(compilation));
        Optional<Compilation> savedCompilation = repository.findById(compilationDto.getId());

        compilation.setId(compilationDto.getId());

        assertThat(savedCompilation).isNotEmpty();
        assertThat(savedCompilation.get())
                .hasFieldOrPropertyWithValue("id", compilation.getId())
                .hasFieldOrPropertyWithValue("title", compilation.getTitle())
                .hasFieldOrPropertyWithValue("pinned", compilation.getPinned())
                .hasFieldOrPropertyWithValue("events", compilation.getEvents());
    }

    @Test
    void delete() {
        Compilation compilation = repository.save(Compilation.builder()
                .events(new HashSet<>(Arrays.asList(event2, event3)))
                .title("Compilation")
                .pinned(false)
                .build());

        Map<Long, Long> views = Map.of(event2.getId(), 0L, event3.getId(), 0L);
        when(statService.getViewsForEvents(anyList(), any())).thenReturn(views);

        List<Compilation> compilations = repository.findAll();

        assertThat(compilations).hasSize(1)
                .contains(compilation);

        service.delete(compilation.getId());

        compilations = repository.findAll();

        assertThat(compilations).isEmpty();
    }

    @Test
    void deleteEvent() {
        Compilation compilation = repository.save(Compilation.builder()
                .events(new HashSet<>(Arrays.asList(event1, event2)))
                .title("Compilation")
                .pinned(false)
                .build());

        Optional<Compilation> compDeleteAddEvent = repository.findById(compilation.getId());

        assertThat(compDeleteAddEvent).isNotEmpty();
        assertThat(compDeleteAddEvent.get().getEvents())
                .hasSize(2)
                .containsAll(List.of(event1, event2));

        service.deleteEvent(compilation.getId(), event1.getId());

        Optional<Compilation> compAfterDeleteEvent = repository.findById(compilation.getId());

        assertThat(compAfterDeleteEvent).isNotEmpty();
        assertThat(compAfterDeleteEvent.get().getEvents())
                .hasSize(1)
                .contains(event2)
                .doesNotContain(event1);
    }

    @Test
    void addEvent() {
        Compilation compilation = repository.save(Compilation.builder()
                .events(new HashSet<>(Arrays.asList(event1, event2)))
                .title("Compilation")
                .pinned(false)
                .build());

        Optional<Compilation> compBeforeAddEvent = repository.findById(compilation.getId());

        assertThat(compBeforeAddEvent).isNotEmpty();
        assertThat(compBeforeAddEvent.get().getEvents())
                .hasSize(2)
                .containsAll(List.of(event1, event2));

        service.addEvent(compilation.getId(), event3.getId());
        Optional<Compilation> compAfterAddEvent = repository.findById(compilation.getId());

        assertThat(compAfterAddEvent).isNotEmpty();
        assertThat(compAfterAddEvent.get().getEvents())
                .hasSize(3)
                .containsAll(List.of(event1, event2, event3));
    }

    @Test
    void pin() {
        Compilation compilation = repository.save(Compilation.builder()
                .events(new HashSet<>(Arrays.asList(event1, event2)))
                .title("Compilation")
                .pinned(false)
                .build());

        Optional<Compilation> compBeforePin = repository.findById(compilation.getId());

        assertThat(compBeforePin).isNotEmpty();
        assertThat(compBeforePin.get()).hasFieldOrPropertyWithValue("pinned", false);

        service.pin(compilation.getId());

        Optional<Compilation> compAfterPin = repository.findById(compilation.getId());

        assertThat(compAfterPin).isNotEmpty();
        assertThat(compAfterPin.get()).hasFieldOrPropertyWithValue("pinned", true);
    }

    @Test
    void unpin() {
        Compilation compilation = repository.save(Compilation.builder()
                .events(new HashSet<>(Arrays.asList(event1, event2)))
                .title("Compilation")
                .pinned(true)
                .build());

        Optional<Compilation> compBeforeUnpin = repository.findById(compilation.getId());

        assertThat(compBeforeUnpin).isNotEmpty();
        assertThat(compBeforeUnpin.get()).hasFieldOrPropertyWithValue("pinned", true);

        service.unpin(compilation.getId());

        Optional<Compilation> compAfterUnpin = repository.findById(compilation.getId());

        assertThat(compAfterUnpin).isNotEmpty();
        assertThat(compAfterUnpin.get()).hasFieldOrPropertyWithValue("pinned", false);
    }
}