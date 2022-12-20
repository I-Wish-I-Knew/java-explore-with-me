package ru.practicum.ewm.ewmService.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.ewmService.model.compilation.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @EntityGraph(attributePaths = {"events"})
    List<Compilation> findAllByPinned(Boolean pinned, Pageable pageable);
}
