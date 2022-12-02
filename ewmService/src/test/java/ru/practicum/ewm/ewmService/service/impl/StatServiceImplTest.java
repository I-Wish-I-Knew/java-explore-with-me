package ru.practicum.ewm.ewmService.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.ewmService.model.category.Category;
import ru.practicum.ewm.ewmService.model.event.Event;
import ru.practicum.ewm.ewmService.model.event.Location;
import ru.practicum.ewm.ewmService.model.event.State;
import ru.practicum.ewm.ewmService.model.statModel.EndpointHitDto;
import ru.practicum.ewm.ewmService.model.user.User;
import ru.practicum.ewm.ewmService.repository.CategoryRepository;
import ru.practicum.ewm.ewmService.repository.EventRepository;
import ru.practicum.ewm.ewmService.repository.UserRepository;
import ru.practicum.ewm.ewmService.service.StatService;
import ru.practicum.ewm.ewmService.statClient.StatClient;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest()
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class StatServiceImplTest {

    private static final String APP = "ewm";
    private static final String BASE_URI_EVENT_VIEW = "/events/{%d}";
    private final StatService service;
    @MockBean
    private final StatClient client;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final CategoryRepository categoryRepository;
    @Autowired
    private final EventRepository eventRepository;

    @Test
    void saveHit() {
        when(client.save(any())).thenReturn(new EndpointHitDto());

        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);

        service.saveHit(mockRequest);

        verify(client, times(1))
                .save(any());
    }

    @Test
    void getViewsForEvents() {
        when(client.get(any(), any(), anyList(), any())).thenReturn(Collections.emptyList());

        Map<Long, Long> viewPoints = service.getViewsForEvents(new ArrayList<>(), false);

        assertThat(viewPoints).isEmpty();

        verify(client, times(0))
                .get(any(), any(), anyList(), any());

        User user = userRepository.save(User.builder().name("name").email("email").build());
        Category category = categoryRepository.save(Category.builder().name("name").build());
        Event event = eventRepository.save(Event.builder()
                .annotation("annotation for event1")
                .title("title of event1")
                .category(category)
                .description("description of event1")
                .eventDate(LocalDateTime.now().plusHours(5))
                .location(new Location(52.41621224781127, 13.596652621929262))
                .paid(true)
                .participantLimit(0)
                .requestModeration(false)
                .initiator(user)
                .state(State.PUBLISHED)
                .build());

        when(client.get(any(), any(), anyList(), any())).thenReturn(Collections.emptyList());

        Map<Long, Long> viewPoints2 = service.getViewsForEvents(Collections.singletonList(event), false);

        assertThat(viewPoints2).isEmpty();

        verify(client, times(1))
                .get(any(), any(), anyList(), any());
    }

    @Test
    void getViewsForEvent() {
        when(client.get(any(), any(), anyList(), any())).thenReturn(Collections.emptyList());

        Long viewPoints = service.getViewsForEvent(Event.builder().build(), false);

        assertThat(viewPoints).isZero();

        verify(client, times(1))
                .get(any(), any(), anyList(), any());
    }
}
