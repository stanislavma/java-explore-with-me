package ru.practicum.ewm.controller.pub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.controller.CalculatedData;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventSearchCriteriaDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.impl.RequestService;
import ru.practicum.ewm.stats.client.client.StatsClient;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Контроллер с методами доступными всем пользователям
 */
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
    public List<EventShortDto> getEvents(@Valid @ModelAttribute EventSearchCriteriaDto sc,
                                         HttpServletRequest request) {

        List<Event> events = eventService.getEventsPublic(sc.getText(), sc.getCategories(),
                sc.getPaid(), sc.getRangeStart(), sc.getRangeEnd(), sc.getOnlyAvailable(),
                sc.getFrom(), sc.getSize(), request.getRemoteAddr());

        Map<Long, Long> viewsMap = getViewsMap(events);
        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(events);

        if (sc.getSort().equalsIgnoreCase("VIEWS")) {
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
        return EventMapper.toFullDto(event, getViewsCount(event), getConfirmedRequestCount(event));
    }

}