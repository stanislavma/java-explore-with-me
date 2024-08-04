package ru.practicum.ewm.controller.authorized;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.service.impl.RequestService;

import java.util.List;

/**
 * REST-контроллер для управления заявками на участие в мероприятиях
 * (создание, отмена, получение)
 */
@Slf4j
@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class RequestPrivateController {
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        ParticipationRequest request = requestService.createRequest(userId, eventId);
        return ParticipationRequestMapper.toDto(request);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        ParticipationRequest request = requestService.cancelRequest(userId, requestId);
        return ParticipationRequestMapper.toDto(request);
    }

    @GetMapping
    public List<ParticipationRequestDto> getUserRequests(@PathVariable Long userId) {
        List<ParticipationRequest> requests = requestService.getUserRequests(userId);
        return ParticipationRequestMapper.toDtoList(requests);
    }

}