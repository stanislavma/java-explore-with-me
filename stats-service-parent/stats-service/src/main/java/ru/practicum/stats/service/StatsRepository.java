package ru.practicum.stats.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.service.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stats, Long> {

    @Query("SELECT s FROM Stats s WHERE s.timestamp BETWEEN :start AND :end " +
            "AND s.uri IN :uris " +
            "AND (:unique IS NULL OR s.ip IS DISTINCT FROM :unique)")

    List<Stats> findStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

}
