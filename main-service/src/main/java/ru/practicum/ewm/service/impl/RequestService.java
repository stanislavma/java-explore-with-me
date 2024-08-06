package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.enums.RequestState;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestService {

    private final RequestRepository requestRepository;
    private final EventService eventService;
    private final UserService userService;

    @Transactional
    public ParticipationRequest createRequest(Long userId, Long eventId) {
        User requester = userService.getById(userId);
        Event event = eventService.getEventById(eventId);

        if (event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Инициатор мероприятия не может подать " +
                    "заявку на участие в своем мероприятии", HttpStatus.CONFLICT);
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ValidationException("Вы не можете участвовать в неопубликованном мероприятии",
                    HttpStatus.CONFLICT);
        }

        if (requestRepository.findByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new ValidationException("Вы уже подали заявку на участие в этом мероприятии",
                    HttpStatus.CONFLICT);
        }

        if (event.getParticipantLimit() != 0 && getConfirmedRequestsCount(eventId) >= event.getParticipantLimit()) {
            throw new ValidationException("Достигнут лимит участников",
                    HttpStatus.CONFLICT);
        }

        // Если лимит участников равен 0 или пре-модерация отключена, заявка автоматически подтверждается
        RequestState requestStatus = (event.getParticipantLimit() == 0 ||
                !event.getRequestModeration()) ? RequestState.CONFIRMED : RequestState.PENDING;

        ParticipationRequest request = ParticipationRequest.builder()
                .event(event)
                .requester(requester)
                .status(requestStatus)
                .created(LocalDateTime.now())
                .build();

        return requestRepository.save(request);
    }

    @Transactional
    public ParticipationRequest cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос на участие не найден"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new ValidationException("Вы можете отменить только свои собственные запросы.",
                    HttpStatus.CONFLICT);
        }

        request.setStatus(RequestState.CANCELED);
        return requestRepository.save(request);
    }

    public List<ParticipationRequest> getUserRequests(Long userId) {
        return requestRepository.findAllByRequesterId(userId);
    }

    public Long getConfirmedRequestsCount(Long eventId) {
        return requestRepository.countByEventIdAndStatus(eventId, RequestState.CONFIRMED);
    }

}