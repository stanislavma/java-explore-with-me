package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.enums.ParticipationRequestState;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipationRequestService {

    private final ParticipationRequestRepository requestRepository;
    private final EventService eventService;
    private final UserService userService;

    @Transactional
    public ParticipationRequest createRequest(Long userId, Long eventId) {
        User requester = userService.getById(userId);
        Event event = eventService.getEventById(eventId);

        if (event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("The initiator of the event cannot submit a request to participate in their event", HttpStatus.BAD_REQUEST);
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ValidationException("You cannot participate in an unpublished event", HttpStatus.BAD_REQUEST);
        }

        if (requestRepository.findByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new ValidationException("You have already submitted a request to participate in this event", HttpStatus.BAD_REQUEST);
        }

        if (event.getParticipantLimit() != 0 &&
                event.getParticipantLimit() <= requestRepository.countByEventIdAndStatus(eventId, ParticipationRequestState.CONFIRMED)) {
            throw new ValidationException("The participant limit has been reached", HttpStatus.BAD_REQUEST);
        }

        ParticipationRequest request = ParticipationRequest.builder()
                .event(event)
                .requester(requester)
                .status(event.getRequestModeration() ? ParticipationRequestState.PENDING : ParticipationRequestState.CONFIRMED)
                .created(LocalDateTime.now())
                .build();

        return requestRepository.save(request);
    }

    @Transactional
    public ParticipationRequest cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Participation request not found"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new ValidationException("You can only cancel your own requests", HttpStatus.BAD_REQUEST);
        }

        request.setStatus(ParticipationRequestState.CANCELED);
        return requestRepository.save(request);
    }

    public List<ParticipationRequest> getUserRequests(Long userId) {
        return requestRepository.findAllByRequesterId(userId);
    }

}