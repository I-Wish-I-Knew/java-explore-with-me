package ru.practicum.ewm.ewmService.model.compilation;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
@Builder
public class NewCompilationDto {
    private Long id;
    @NotBlank
    private String title;
    private Boolean pinned;
    private Set<Event> events;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Event {
        private Long id;
    }
}
