package ru.practicum.ewm.ewmService.model.compilation;

import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.ewmService.model.event.EventShortDto;

import java.util.Set;

@Data
@Builder
public class CompilationDto {
    private Long id;
    private Set<EventShortDto> events;
    private Boolean pinned;
    private String title;
}
