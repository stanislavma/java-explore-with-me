package ru.practicum.ewm.controller.admin;

import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.controller.CalculatedData;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.impl.RequestService;
import ru.practicum.ewm.stats.client.client.StatsClient;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Контроллер для администратора мероприятий
 */
@RestController
@RequestMapping("/admin/events")
public class EventAdminController extends CalculatedData {

    private final EventService eventService;

    public EventAdminController(EventService eventService, StatsClient statsClient, RequestService requestService) {
        super(statsClient, requestService);
        this.eventService = eventService;
    }

    /**
     * Получение мероприятий
     */
    @GetMapping
    public List<EventFullDto> getEvents(
            HttpServletRequest request,
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {

        List<Event> events = eventService.getEventsForAdmin(users, states, categories, rangeStart, rangeEnd, from, size, request.getRemoteAddr());

        Map<Long, Long> viewsMap = getViewsMap(events);
        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(events);

        return EventMapper.toFullDto(events, viewsMap, confirmedRequestsMap);
    }

    /**
     * Обновление существующего мероприятия
     */
    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        Event updatedEvent = eventService.updateEventByAdmin(eventId, updateEventAdminRequest);

        long viewsCount = getViewsCount(updatedEvent);
        long confirmedRequestCount = getConfirmedRequestCount(updatedEvent);

        return EventMapper.toFullDto(updatedEvent, viewsCount, confirmedRequestCount);
    }

}