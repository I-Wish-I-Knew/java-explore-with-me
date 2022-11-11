package ru.practicum.ewm.statisticService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.statisticService.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<EndpointHit, Long> {

    Long countByTimestampAfterAndTimestampBeforeAndUri(LocalDateTime start,
                                                       LocalDateTime end,
                                                       String uri);

    @Query("select count(distinct e.ip) from EndpointHit e " +
            "where e.timestamp > :start " +
            "and  e.timestamp < :end " +
            "and e.uri = :uri ")
    Long countByTimestampAndUriAndIpUnique(@Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end,
                                           @Param("uri") String uri);

    @Query("select e.uri, e.app from EndpointHit e " +
            "where e.uri in :uris")
    List<Object[]> findApps(@Param("uris") List<String> uris);
}
