package ru.practicum.ewm.statisticService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.statisticService.model.EndpointHit;
import ru.practicum.ewm.statisticService.model.ViewPoints;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select new ru.practicum.ewm.statisticService.model.ViewPoints(e.app, e.uri, count(e.ip)) " +
            "from EndpointHit e " +
            "where e.timestamp > :start " +
            "and  e.timestamp < :end " +
            "and e.uri in :uris " +
            "group by e.uri, e.app")
    List<ViewPoints> countByTimestampAndUris(@Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end,
                                             @Param("uris") List<String> uris);

    @Query("select new ru.practicum.ewm.statisticService.model.ViewPoints(e.app, e.uri, count(distinct e.ip)) " +
            "from EndpointHit e " +
            "where e.timestamp > :start " +
            "and  e.timestamp < :end " +
            "and e.uri in :uris " +
            "group by e.uri, e.app")
    List<ViewPoints> countByTimestampAndUriSAndIpUnique(@Param("start") LocalDateTime start,
                                                        @Param("end") LocalDateTime end,
                                                        @Param("uris") List<String> uris);
}
