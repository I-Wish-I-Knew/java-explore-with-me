package ru.practicum.ewm.statisticService.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.statisticService.model.EndpointHit;
import ru.practicum.ewm.statisticService.model.EndpointHitDto;
import ru.practicum.ewm.statisticService.model.ViewPoints;
import ru.practicum.ewm.statisticService.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class StatServiceImplTest {

    private final StatService service;
    @Autowired
    private final StatRepository repository;
    private static final String APP = "ewm";
    private static final String BASE_URI_EVENT_VIEW = "/events/{%d}";
    private String ip;

    @BeforeEach
    void setUp() {
        Random r = new Random();
        ip = r.nextInt(256) + "." +
                r.nextInt(256) + "." +
                r.nextInt(256) + "." +
                r.nextInt(256);
    }

    @Test
    void save() {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app(APP)
                .ip(ip)
                .uri(String.format(BASE_URI_EVENT_VIEW, 1))
                .timestamp(LocalDateTime.now())
                .build();

        EndpointHitDto savedEndPointHit = service.save(endpointHitDto);

        Optional<EndpointHit> endpointHit = repository.findById(savedEndPointHit.getId());

        assertThat(endpointHit).isNotEmpty();
        assertThat(endpointHit.get())
                .hasFieldOrPropertyWithValue("app", endpointHitDto.getApp())
                .hasFieldOrPropertyWithValue("ip", endpointHitDto.getIp())
                .hasFieldOrPropertyWithValue("uri", endpointHitDto.getUri())
                .hasFieldOrPropertyWithValue("timestamp", endpointHitDto.getTimestamp());
    }

    @Test
    void get() {
        EndpointHit endpointHit = repository.save(EndpointHit.builder()
                .app(APP)
                .ip(ip)
                .uri(String.format(BASE_URI_EVENT_VIEW, 1))
                .timestamp(LocalDateTime.now())
                .build());

        String start = getDateForRequest(endpointHit.getTimestamp().minusDays(1));
        String end = getDateForRequest(endpointHit.getTimestamp().plusDays(1));

        List<ViewPoints> viewPoints = service.get(start, end, Collections.singletonList(endpointHit.getUri()),
                false);

        assertThat(viewPoints).hasSize(1)
                .contains(ViewPoints.builder()
                        .app(endpointHit.getApp())
                        .uri(endpointHit.getUri())
                        .hits(1L)
                        .build());
    }

    @Test
    void getUnique() {
        EndpointHit endpointHit1 = repository.save(EndpointHit.builder()
                .app(APP)
                .ip(ip)
                .uri(String.format(BASE_URI_EVENT_VIEW, 1))
                .timestamp(LocalDateTime.now())
                .build());

        repository.save(EndpointHit.builder()
                .app(APP)
                .ip(endpointHit1.getIp())
                .uri(String.format(BASE_URI_EVENT_VIEW, 1))
                .timestamp(LocalDateTime.now())
                .build());

        String start = getDateForRequest(endpointHit1.getTimestamp().minusDays(1));
        String end = getDateForRequest(endpointHit1.getTimestamp().plusDays(1));

        List<ViewPoints> viewPoints = service.get(start, end, Collections.singletonList(endpointHit1.getUri()),
                true);

        assertThat(viewPoints).hasSize(1)
                .contains(ViewPoints.builder()
                        .app(endpointHit1.getApp())
                        .uri(endpointHit1.getUri())
                        .hits(1L)
                        .build());
    }

    private String getDateForRequest(LocalDateTime date) {
        return date.withNano(0).toString().replace("T", " ");
    }
}