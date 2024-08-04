package ru.practicum.ewm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.service.impl.RequestService;
import ru.practicum.ewm.stats.client.client.StatsClient;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static ru.practicum.ewm.common.Constants.DATE_TIME_FORMATTER;

/**
 * Класс для получения вычисляемых данных по мероприятиям
 */
@Slf4j
@Component
public class CalculatedData {

    private final StatsClient statsClient;
    private final RequestService requestService;

    public CalculatedData(StatsClient statsClient, RequestService requestService) {
        this.statsClient = statsClient;
        this.requestService = requestService;
    }

    protected Map<Long, Long> getViewsMap(List<Event> events) {
        HashMap<Long, Long> viewsMap = new HashMap<>();

        if (events == null) {
            return viewsMap;
        }

        for (Event event : events) {
            viewsMap.put(event.getId(), getViewsCount(event));
        }

        return viewsMap;
    }

    protected long getViewsCount(Event event) {
        long viewsCount = 0L;
        try {
            ResponseEntity<Object> response = statsClient.getStats(
                    event.getCreatedOn().format(DATE_TIME_FORMATTER),
                    event.getEventDate().format(DATE_TIME_FORMATTER),
                    List.of("/events/" + event.getId()),
                    true);
            List<LinkedHashMap<String, Object>> viewStatsList = (List<LinkedHashMap<String, Object>>) response.getBody();
            if (viewStatsList != null) {
                viewsCount = viewStatsList.stream()
                        .mapToLong(stats -> ((Number) stats.get("hits")).longValue())
                        .sum();
            }
        } catch (Exception e) {
            log.error("Failed to get view stats for event with id {}", event.getId(), e);
        }
        return viewsCount;
    }

    protected Map<Long, Long> getConfirmedRequestsMap(List<Event> events) {
        HashMap<Long, Long> confirmedRequestMap = new HashMap<>();

        if (events == null) {
            return confirmedRequestMap;
        }

        for (Event event : events) {
            confirmedRequestMap.put(event.getId(), getConfirmedRequestCount(event));
        }

        return confirmedRequestMap;
    }

    protected long getConfirmedRequestCount(Event event) {
        return requestService.getConfirmedRequestsCount(event.getId());
    }

}
