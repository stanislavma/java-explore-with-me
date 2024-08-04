package ru.practicum.ewm.controller.pub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.controller.CalculatedData;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.impl.RequestService;
import ru.practicum.ewm.stats.client.client.StatsClient;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.ewm.common.Constants.DATE_TIME_FORMAT_PATTERN;

@Slf4j
@RestController
@RequestMapping("/events")
public class EventPublicController extends CalculatedData {

    private final EventService eventService;

    public EventPublicController(EventService eventService, StatsClient statsClient, RequestService requestService) {
        super(statsClient, requestService);
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventShortDto> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT_PATTERN) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT_PATTERN) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false, defaultValue = "VIEWS") String sort,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        List<Event> events = eventService.getEventsPublic(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, from, size, request.getRemoteAddr());

        Map<Long, Long> viewsMap = getViewsMap(events);
        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(events);

        if (sort.equalsIgnoreCase("VIEWS")) {
            // sort by views
            events.sort((e1, e2) -> {
                long views1 = viewsMap.getOrDefault(e1.getId(), 0L);
                long views2 = viewsMap.getOrDefault(e2.getId(), 0L);
                return Long.compare(views2, views1);
            });
        } else {
            // sort by event date
            events.sort((e1, e2) -> e2.getEventDate().compareTo(e1.getEventDate()));
        }

        return events.stream()
                .map(event -> EventMapper.toShortDto(
                        event,
                        viewsMap.getOrDefault(event.getId(), 0L),
                        confirmedRequestsMap.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable Long id, HttpServletRequest request) {
        Event event = eventService.getPublishedEvent(id, request.getRemoteAddr());

        long viewsCount = getViewsCount(event);
        long confirmedRequestCount = getConfirmedRequestCount(event);

        return EventMapper.toFullDto(event, viewsCount, confirmedRequestCount);
    }

}