package ru.practicum.ewm.ewmService.service;

import ru.practicum.ewm.ewmService.model.compilation.CompilationDto;
import ru.practicum.ewm.ewmService.model.compilation.NewCompilationDto;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size);

    CompilationDto get(Long id);

    NewCompilationDto save(NewCompilationDto newCompilationDto);

    void delete(Long id);

    void deleteEvent(Long id, Long eventId);

    void addEvent(Long id, Long eventId);

    void pin(Long id);

    void unpin(Long id);
}
