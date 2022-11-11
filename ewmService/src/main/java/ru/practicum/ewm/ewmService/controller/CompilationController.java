package ru.practicum.ewm.ewmService.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.ewmService.model.compilation.CompilationDto;
import ru.practicum.ewm.ewmService.model.compilation.NewCompilationDto;
import ru.practicum.ewm.ewmService.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
public class CompilationController {

    private final CompilationService service;

    public CompilationController(CompilationService service) {
        this.service = service;
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getAll(@RequestParam(required = false) Boolean pinned,
                                       @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                       @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Get all events compilations from {} size {}", from, size);
        return service.getAll(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto get(@PathVariable Long compId) {
        log.info("Get events compilation {}", compId);
        return service.get(compId);
    }

    @PostMapping("/admin/compilations")
    public NewCompilationDto save(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Save events compilation {}", newCompilationDto);
        return service.save(newCompilationDto);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    public void delete(@PathVariable Long compId) {
        log.info("Delete events compilation {}", compId);
        service.delete(compId);
    }

    @DeleteMapping("/admin/compilations/{compId}/events/{eventId}")
    public void deleteEvent(@PathVariable Long compId,
                            @PathVariable Long eventId) {
        log.info("Delete event {} in compilation {}", eventId, compId);
        service.deleteEvent(compId, eventId);
    }

    @PatchMapping("/admin/compilations/{compId}/events/{eventId}")
    public void addEvent(@PathVariable Long compId,
                         @PathVariable Long eventId) {
        log.info("Add event {} in compilation {}", eventId, compId);
        service.addEvent(compId, eventId);
    }

    @DeleteMapping("/admin/compilations/{compId}/pin")
    public void unpin(@PathVariable Long compId) {
        log.info("Unpin compilation {}", compId);
        service.unpin(compId);
    }

    @PatchMapping("/admin/compilations/{compId}/pin")
    public void pin(@PathVariable Long compId) {
        log.info("Pin compilation {}", compId);
        service.pin(compId);
    }
}
