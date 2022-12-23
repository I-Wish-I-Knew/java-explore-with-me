package ru.practicum.ewm.ewmService.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.ewmService.model.request.Request;
import ru.practicum.ewm.ewmService.model.request.StateRequest;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @EntityGraph(attributePaths = {"event", "requester"})
    List<Request> findAllByRequesterId(Long requesterId);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByEventIdAndStatus(Long eventId, StateRequest status);

    Optional<Request> findByIdAndEventIdAndEventInitiatorId(Long id, Long eventId, Long requesterId);

    Optional<Request> findByRequesterIdAndEventId(Long requesterId, Long eventId);

    Optional<Request> findByIdAndRequesterId(Long id, Long requesterId);

    Boolean existsByEventIdAndRequesterId(Long eventId, Long requesterId);

    Integer countAllByStatusAndEventId(StateRequest status, Long eventId);

}
