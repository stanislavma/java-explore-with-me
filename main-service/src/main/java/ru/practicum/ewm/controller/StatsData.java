package ru.practicum.ewm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.stats.ViewStatsDto;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.stats.client.client.StatsClient;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hibernate.type.LocalDateTimeType.FORMATTER;

@Slf4j
@Component
public class StatsData {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsClient statsClient;

    public StatsData(StatsClient statsClient) {
        this.statsClient = statsClient;
    }

    protected Map<Long, Long> getViewsMap(List<Event> events) {
        HashMap<Long, Long> viewsMap = new HashMap<>();

        for (Event event : events) {
            viewsMap.put(event.getId(), getViewsCount(event));
        }

        return viewsMap;
    }

    // TODO
    protected Map<Long, Long> getConfirmedRequestsMap(List<Event> events) {
        return new HashMap<>(); // Заглушка
    }

    protected long getViewsCount(Event event) {
        long viewsCount = 0L;
        try {
            ResponseEntity<Object> response = statsClient.getStats(
                    event.getEventDate().format(dateTimeFormatter),
                    event.getEventDate().format(dateTimeFormatter),
                    List.of("/events/" + event.getId()),
                    true);
            List<ViewStatsDto> viewStatsDtoList = (List<ViewStatsDto>) response.getBody();
            if (viewStatsDtoList != null) {
                viewsCount = viewStatsDtoList.stream()
                        .mapToLong(ViewStatsDto::getHits) // Предполагается, что ViewStatsDto имеет метод getHits для получения количества просмотров
                        .sum();
            }
        } catch (Exception e) {
            log.error("Failed to get view stats for event with id {}", event.getId(), e);
        }
        return viewsCount;
    }

    protected long getConfirmedRequestCount(Event event) {
        return 0;
//        List<ViewStatsDto> viewConfirmedRequestDtoList;
//        long confirmedRequestCount = 0L;
//        try {
//            viewConfirmedRequestDtoList = (List<ViewStatsDto>) statsClient.getStats(
//                    event.getEventDate().format(FORMATTER),
//                    event.getEventDate().format(FORMATTER),
//                    List.of("/events/" + event.getId()),
//                    true);
//            confirmedRequestCount = viewConfirmedRequestDtoList.size();
//        } catch (Exception e) {
//            log.error("Failed to get viewConfirmedRequestDtoList stats for event with id {}", event.getId(), e);
//        }
//        return confirmedRequestCount;
    }

}
