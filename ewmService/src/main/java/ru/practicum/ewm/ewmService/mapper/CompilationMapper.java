package ru.practicum.ewm.ewmService.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.ewmService.model.compilation.Compilation;
import ru.practicum.ewm.ewmService.model.compilation.CompilationDto;
import ru.practicum.ewm.ewmService.model.compilation.NewCompilationDto;
import ru.practicum.ewm.ewmService.model.event.Event;
import ru.practicum.ewm.ewmService.model.event.EventShortDto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {

    public static Compilation toCompilation(NewCompilationDto newCompilationDto,
                                            Set<Event> events) {
        return Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.getPinned())
                .events(events)
                .build();
    }

    public static CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> eventShortDtoList) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .events(new HashSet<>(eventShortDtoList))
                .title(compilation.getTitle())
                .build();
    }

    public static NewCompilationDto toNewCompilationDto(Compilation compilation) {
        return NewCompilationDto.builder()
                .id(compilation.getId())
                .events(compilation.getEvents().stream()
                        .map(event -> new NewCompilationDto.Event(event.getId()))
                        .collect(Collectors.toSet()))
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }
}
