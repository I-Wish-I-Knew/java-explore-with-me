package ru.practicum.ewm.ewmService.model.compilation;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewm.ewmService.model.event.EventShortDto;

import java.util.Set;

@Data
@Builder
@Jacksonized
public class CompilationDto {
    private Long id;
    private Set<EventShortDto> events;
    private Boolean pinned;
    private String title;
}
