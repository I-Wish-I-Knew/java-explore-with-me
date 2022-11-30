package ru.practicum.ewm.ewmService.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.ewmService.exception.NotFoundException;
import ru.practicum.ewm.ewmService.mapper.CompilationMapper;
import ru.practicum.ewm.ewmService.model.compilation.Compilation;
import ru.practicum.ewm.ewmService.model.compilation.CompilationDto;
import ru.practicum.ewm.ewmService.model.compilation.NewCompilationDto;
import ru.practicum.ewm.ewmService.model.event.Event;
import ru.practicum.ewm.ewmService.repository.CompilationRepository;
import ru.practicum.ewm.ewmService.repository.EventRepository;
import ru.practicum.ewm.ewmService.service.CompilationService;
import ru.practicum.ewm.ewmService.service.EventService;
import ru.practicum.ewm.ewmService.utility.Page;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.ewm.ewmService.utility.Constants.COMPILATION_NOT_FOUND;
import static ru.practicum.ewm.ewmService.utility.Constants.EVENT_NOT_FOUND;

@Transactional(readOnly = true)
@Service
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository repository;
    private final EventRepository eventRepository;
    private final EventService eventService;

    public CompilationServiceImpl(CompilationRepository repository, EventRepository eventRepository,
                                  EventService eventService) {
        this.repository = repository;
        this.eventRepository = eventRepository;
        this.eventService = eventService;
    }

    @Override
    public List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations;
        if (pinned != null) {
            compilations = repository.findAllByPinned(pinned, Page.of(from, size));
        } else {
            compilations = repository.findAll(Page.of(from, size)).getContent();
        }
        return compilations.stream()
                .map(compilation -> CompilationMapper.toCompilationDto(compilation,
                        eventService.convertToListEventShortDto(new ArrayList<>(compilation.getEvents()), false)))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto get(Long id) {
        Compilation compilation = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(COMPILATION_NOT_FOUND, id)));
        return CompilationMapper.toCompilationDto(compilation,
                eventService.convertToListEventShortDto(new ArrayList<>(compilation.getEvents()), false));
    }

    @Transactional
    @Override
    public CompilationDto save(NewCompilationDto newCompilationDto) {
        List<Long> ids = new ArrayList<>(newCompilationDto.getEvents()).stream()
                .map(NewCompilationDto.Event::getId)
                .collect(Collectors.toList());
        Set<Event> events = new HashSet<>(eventRepository.findAllByIdIn(ids));
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto, events);
        compilation = repository.save(compilation);
        return CompilationMapper.toCompilationDto(compilation,
                eventService.convertToListEventShortDto(new ArrayList<>(compilation.getEvents()), false));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    @Override
    public void deleteEvent(Long id, Long eventId) {
        Compilation compilation = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(COMPILATION_NOT_FOUND, id)));
        Event event = compilation.getEvents().stream()
                .filter(e -> e.getId().equals(eventId))
                .findFirst()
                .orElse(null);
        compilation.getEvents().remove(event);
        repository.save(compilation);
    }

    @Transactional
    @Override
    public void addEvent(Long id, Long eventId) {
        Compilation compilation = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(COMPILATION_NOT_FOUND, id)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format(EVENT_NOT_FOUND, eventId)));
        compilation.getEvents().add(event);
        repository.save(compilation);
    }

    @Transactional
    @Override
    public void pin(Long id) {
        Compilation compilation = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(COMPILATION_NOT_FOUND, id)));
        compilation.setPinned(true);
        repository.save(compilation);
    }

    @Transactional
    @Override
    public void unpin(Long id) {
        Compilation compilation = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(COMPILATION_NOT_FOUND, id)));
        compilation.setPinned(false);
        repository.save(compilation);
    }
}
