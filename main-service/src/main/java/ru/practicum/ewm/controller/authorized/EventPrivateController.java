package ru.practicum.ewm.controller.authorized;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.controller.CalculatedData;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.event.UpdateEventUserRequest;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.impl.RequestService;
import ru.practicum.ewm.stats.client.client.StatsClient;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST-контроллер для работы с мероприятиями пользователя
 * (создание, обновление, получение)
 */
@Slf4j
@RestController
@RequestMapping("/users/{userId}/events")
public class EventPrivateController extends CalculatedData {

    private final EventService eventService;

    public EventPrivateController(StatsClient statsClient, EventService eventService, RequestService requestService) {
        super(statsClient, requestService);
        this.eventService = eventService;
    }

    /**
     * Создание мероприятия
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        Event event = eventService.add(userId, newEventDto);
        return EventMapper.toFullDto(event, 0L, 0L);
    }

    /**
     * Обновление мероприятия
     */
    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {

        Event updatedEvent = eventService.updateEventByInitiator(userId, eventId, updateEventUserRequest);
        return EventMapper.toFullDto(updatedEvent, getViewsCount(updatedEvent), getConfirmedRequestCount(updatedEvent));
    }

    /**
     * Получение всех мероприятий пользователя
     */
    @GetMapping
    public List<EventFullDto> getEventsByUser(@PathVariable Long userId,
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size) {
        List<Event> events = eventService.getEventsByInitiator(userId, from, size);

        Map<Long, Long> viewsMap = getViewsMap(events);
        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(events);

        return events.stream()
                .map(event -> EventMapper.toFullDto(
                        event,
                        viewsMap.getOrDefault(event.getId(), 0L),
                        confirmedRequestsMap.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    /**
     * Получение мероприятия пользователя
     */
    @GetMapping("/{eventId}")
    public EventFullDto getEventByUser(@PathVariable Long userId,
                                       @PathVariable Long eventId) {
        Event event = eventService.getEventByInitiator(userId, eventId);
        return EventMapper.toFullDto(event, getViewsCount(event), getConfirmedRequestCount(event));
    }

}