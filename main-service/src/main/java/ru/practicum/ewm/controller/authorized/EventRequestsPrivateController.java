package ru.practicum.ewm.controller.authorized;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.service.EventService;

import javax.validation.Valid;
import java.util.List;

/**
 * REST-контроллер для работы с заявками пользователей по участию в мероприятии
 * (получение, обновление статуса)
 */
@Slf4j
@RestController
@RequestMapping("/users/{userId}/events/{eventId}/requests")
@RequiredArgsConstructor
public class EventRequestsPrivateController {

    private final EventService eventService;
    private final ParticipationRequestMapper requestMapper;

    /**
     * Получение запросов на участие в мероприятии
     */
    @GetMapping
    public List<ParticipationRequestDto> getEventParticipationRequests(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        List<ParticipationRequest> requests = eventService.getEventParticipationRequests(userId, eventId);
        return ParticipationRequestMapper.toDtoList(requests);
    }

    /**
     * Обновление статуса запроса на участие в мероприятии
     */
    @PatchMapping
    public EventRequestStatusUpdateResult updateEventRequestsStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid EventRequestStatusUpdateRequest updateRequest) {
        return eventService.updateEventRequestsStatus(userId, eventId, updateRequest);
    }

}