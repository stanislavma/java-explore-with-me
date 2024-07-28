package ru.practicum.stats.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.service.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT new ru.practicum.stats.service.model.EndpointHit(s.app, s.uri, COUNT(s.id)) " +
            "FROM EndpointHit s WHERE s.hitDate BETWEEN :start AND :end " +
            "AND s.uri IN :uris GROUP BY s.app, s.uri")
    List<EndpointHit> findStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.stats.service.model.EndpointHit(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM EndpointHit s WHERE s.hitDate BETWEEN :start AND :end " +
            "AND s.uri IN :uris GROUP BY s.app, s.uri")
    List<EndpointHit> findUniqueStats(LocalDateTime start, LocalDateTime end, List<String> uris);

}
