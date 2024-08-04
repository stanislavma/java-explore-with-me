package ru.practicum.ewm.controller.authorized;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.controller.StatsData;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.NewEventDto;
import ru.practicum.ewm.dto.UpdateEventUserRequest;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.stats.client.client.StatsClient;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/events")
public class EventPrivateController extends StatsData {

    private final EventService eventService;

    public EventPrivateController(StatsClient statsClient, EventService eventService) {
        super(statsClient);
        this.eventService = eventService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        Event event = eventService.add(userId, newEventDto);

        return EventMapper.toFullDto(event, 0L, 0L);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        Event updatedEvent = eventService.updateEventByInitiator(userId, eventId, updateEventUserRequest);

        long viewsCount = getViewsCount(updatedEvent);
        long confirmedRequestCount = getConfirmedRequestCount(updatedEvent);

        return EventMapper.toFullDto(updatedEvent, viewsCount, confirmedRequestCount);
    }

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

    @GetMapping("/{eventId}")
    public EventFullDto getEventByUser(@PathVariable Long userId,
                                       @PathVariable Long eventId) {
        Event event = eventService.getEventByInitiator(userId, eventId);

        long viewsCount = getViewsCount(event);
        long confirmedRequestCount = getConfirmedRequestCount(event);
        return EventMapper.toFullDto(event, viewsCount, confirmedRequestCount);
    }


}