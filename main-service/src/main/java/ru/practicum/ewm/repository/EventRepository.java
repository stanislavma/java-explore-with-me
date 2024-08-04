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

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByInitiator(User user, Pageable pageable);

    Optional<Event> findByIdAndInitiator(Long eventId, User initiator);

    @Query("SELECT e FROM Event e WHERE " +
            "(COALESCE(:users, NULL) IS NULL OR e.initiator.id IN :users) AND " +
            "(COALESCE(:states, NULL) IS NULL OR e.state IN :states) AND " +
            "(COALESCE(:categories, NULL) IS NULL OR e.category.id IN :categories) AND " +
            "(:start IS NULL OR e.eventDate >= :start) AND " +
            "(:end IS NULL OR e.eventDate <= :end)")
    List<Event> findAllByCriteria(@Param("users") List<Long> users,
                                  @Param("states") List<EventState> states,
                                  @Param("categories") List<Long> categories,
                                  @Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end,
                                  Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (:text IS NULL OR (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "                    OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')))) " +
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

    Optional<Event> findByIdAndState(Long id, EventState state);

}