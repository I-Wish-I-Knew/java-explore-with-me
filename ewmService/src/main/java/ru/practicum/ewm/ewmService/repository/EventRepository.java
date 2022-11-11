package ru.practicum.ewm.ewmService.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.ewmService.model.event.Event;
import ru.practicum.ewm.ewmService.model.event.State;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByIdIn(List<Long> ids);

    Boolean existsByCategoryId(Long categoryId);

    Optional<Event> findByIdAndState(Long id, State state);

    Optional<Event> findByIdAndInitiatorId(Long id, Long initiatorId);

    List<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    @Query(value = "select ev.event_id, (select count(pr.*) " +
            "from events e " +
            "left join participation_requests pr on e.event_id = pr.event_id " +
            "where pr.status = :status " +
            "and e.event_id in :eventIds) " +
            "from events ev " +
            "where ev.event_id in :eventIds  " +
            "group by ev.event_id", nativeQuery = true)
    List<Long[]> countAllByStatusAndEventIdIn(@Param("status") String status,
                                              @Param("eventIds") List<Long> eventIds);
}
