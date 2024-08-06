package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.event.UpdateEventAdminRequest;
import ru.practicum.ewm.dto.event.UpdateEventUserRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.ParticipationRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    Event add(Long userId, NewEventDto newEventDto);

    Event updateEventByInitiator(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    Event updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<Event> getEventsForAdmin(List<Long> users, List<String> states, List<Long> categories,
                                  String rangeStart, String rangeEnd, int from, int size, String remoteAddr);

    Event getEventById(Long eventId);

    List<Event> getEventsByInitiator(Long userId, int from, int size);

    Event getEventByInitiator(Long userId, Long eventId);

    List<Event> getEventsPublic(String text, List<Long> categories, Boolean paid,
                                LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                Boolean onlyAvailable,
                                int from, int size, String ip);

    Event getPublishedEvent(Long id, String ip);

    List<ParticipationRequest> getEventParticipationRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateEventRequestsStatus(Long userId, Long eventId,
                                                             EventRequestStatusUpdateRequest updateRequest);

}