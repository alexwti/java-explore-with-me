package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.EndPointHit;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndPointHit, Long> {

    @Query(value = "SELECT new ru.practicum.model.ViewStats(s.app, s.uri, COUNT(s.ip)) " +
            "FROM EndPointHit s where s.timestamp BETWEEN :start AND :end AND (s.uri IN (:uris) OR :uris = null)" +
            "GROUP BY s.app, s.uri ORDER BY COUNT(s.ip) DESC"
    )
    List<ViewStats> getStatsByDate(@Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end,
                                   @Param("uris") List<String> uris);

    @Query(value = "SELECT new ru.practicum.model.ViewStats(s.app, s.uri, COUNT(distinct s.ip)) " +
            "FROM EndPointHit s where s.timestamp BETWEEN :start AND :end AND (s.uri in :uris OR :uris = null) " +
            "GROUP BY s.app, s.uri ORDER BY COUNT(distinct s.ip) DESC"
    )
    List<ViewStats> getStatsByDateUniqueIp(@Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end,
                                           @Param("uris") List<String> uris);
}
