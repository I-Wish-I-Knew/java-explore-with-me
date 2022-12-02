package ru.practicum.ewm.ewmService.statClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.ewm.ewmService.model.statModel.EndpointHitDto;
import ru.practicum.ewm.ewmService.model.statModel.ViewPoints;

import java.util.List;

@Service
public class StatClient {
    private final WebClient webClient;

    @Autowired
    public StatClient(@Value("${statisticService.url}") String serverUrl) {
        webClient = WebClient.builder()
                .baseUrl(serverUrl)
                .build();
    }

    public EndpointHitDto save(EndpointHitDto endpointHitDto) {
        return webClient
                .post()
                .uri("/hit")
                .body(BodyInserters.fromValue(endpointHitDto))
                .retrieve()
                .bodyToMono(EndpointHitDto.class)
                .block();
    }

    public List<ViewPoints> get(String start, String end,
                                List<String> uris, Boolean unique) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("uris", commaSeparatedUris(uris))
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .bodyToFlux(ViewPoints.class)
                .collectList()
                .block();
    }

    private String commaSeparatedUris(List<String> uris) {
        return String.join(", ", uris)
                .replace("{", "")
                .replace("}", "");
    }
}
