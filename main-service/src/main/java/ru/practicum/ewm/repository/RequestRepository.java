package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.enums.RequestState;
import ru.practicum.ewm.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByRequesterId(Long requesterId);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    Optional<ParticipationRequest> findByRequesterIdAndEventId(Long requesterId, Long eventId);

    Long countByEventIdAndStatus(Long eventId, RequestState status);

}