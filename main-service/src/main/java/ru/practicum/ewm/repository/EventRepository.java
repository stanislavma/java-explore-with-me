package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.comments LEFT JOIN FETCH e.initiator LEFT JOIN FETCH e.category WHERE e.id = :eventId")
    Optional<Event> findByIdWithDetails(@Param("eventId") Long eventId);

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.comments LEFT JOIN FETCH e.initiator " +
            "LEFT JOIN FETCH e.category WHERE e.initiator.id = :userId")
    List<Event> findByInitiatorIdWithComments(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.comments LEFT JOIN FETCH e.initiator " +
            "LEFT JOIN FETCH e.category WHERE e.id = :eventId AND e.initiator = :initiator")
    Optional<Event> findByIdAndInitiator(@Param("eventId") Long eventId, @Param("initiator") User initiator);

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.comments WHERE " +
            "(COALESCE(:users, NULL) IS NULL OR e.initiator.id IN :users) AND " +
            "(COALESCE(:states, NULL) IS NULL OR e.state IN :states) AND " +
            "(COALESCE(:categories, NULL) IS NULL OR e.category.id IN :categories) AND " +
            "((cast(:start as date)) IS NULL OR e.eventDate >= :start) AND " +
            "((cast(:end as date)) IS NULL OR e.eventDate <= :end)")
    List<Event> findAllByCriteria(@Param("users") List<Long> users,
                                  @Param("states") List<EventState> states,
                                  @Param("categories") List<Long> categories,
                                  @Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end,
                                  Pageable pageable);

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.comments " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (:text IS NULL OR (LOWER(e.annotation) LIKE LOWER(CONCAT('%', COALESCE(:text, ''), '%')) " +
            "                    OR LOWER(e.description) LIKE LOWER(CONCAT('%', COALESCE(:text, ''), '%')))) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (CAST(e.eventDate AS timestamp) >= CAST(:rangeStart AS timestamp)) " +
            "AND (cast(:rangeEnd as date)) is null OR CAST(e.eventDate AS timestamp) <= CAST(:rangeEnd AS timestamp)")
    List<Event> findPublishedEvents(@Param("text") String text,
                                    @Param("categories") List<Long> categories,
                                    @Param("paid") Boolean paid,
                                    @Param("rangeStart") LocalDateTime rangeStart,
                                    @Param("rangeEnd") LocalDateTime rangeEnd,
                                    Pageable pageable);

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.comments LEFT JOIN FETCH e.initiator " +
            "LEFT JOIN FETCH e.category WHERE e.id = :id AND e.state = :state")
    Optional<Event> findByIdAndState(@Param("id") Long id, @Param("state") EventState state);

    @Query("SELECT DISTINCT e FROM Event e LEFT JOIN FETCH e.comments LEFT JOIN FETCH e.initiator " +
            "LEFT JOIN FETCH e.category WHERE e.id IN :eventIds")
    List<Event> findAllByIdWithInitiatorAndCategory(@Param("eventIds") Set<Long> eventIds);

}