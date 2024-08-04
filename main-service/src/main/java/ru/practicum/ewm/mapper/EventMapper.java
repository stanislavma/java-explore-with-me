package ru.practicum.ewm.mapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.LocationDto;
import ru.practicum.ewm.dto.NewEventDto;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Location;
import ru.practicum.ewm.stats.client.client.StatsClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class EventMapper {

    private static final StatsClient statsClient = null;

    public static EventFullDto toFullDto(Event event, Long viewsCount, Long confirmedRequestsCount) {
        return EventFullDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .category(CategoryMapper.toDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .location(toLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .views(viewsCount)
                .confirmedRequests(confirmedRequestsCount)
                .build();
    }

    public static List<EventFullDto> toFullDto(List<Event> events,
                                               Map<Long, Long> viewsMap,
                                               Map<Long, Long> confirmedRequestsMap) {
        return events.stream()
                .map(event -> toFullDto(event,
                        viewsMap.getOrDefault(event.getId(), 0L),
                        confirmedRequestsMap.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    private static EventShortDto toShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .views(0L)
                .confirmedRequests(0L)
                .build();
    }

    public static EventShortDto toShortDto(Event event, Long viewsCount, Long confirmedRequestsCount) {
        EventShortDto dto = toShortDto(event);
        dto.setViews(viewsCount);
        dto.setConfirmedRequests(confirmedRequestsCount);
        return dto;
    }

    public static Event toEntity(NewEventDto newEventDto) {
        return Event.builder()
                .title(newEventDto.getTitle())
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .location(toLocation(newEventDto.getLocation()))
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .build();
    }

    private static LocationDto toLocationDto(Location location) {
        return new LocationDto(location.getLat(), location.getLon());
    }

    private static Location toLocation(LocationDto locationDto) {
        return new Location(locationDto.getLat(), locationDto.getLon());
    }

}