package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.dto.stats.EndpointHitDto;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.enums.NewStateAction;
import ru.practicum.ewm.enums.ParticipationRequestState;
import ru.practicum.ewm.enums.UpdateStateAction;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.LocationRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.service.CategoryService;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.UserService;
import ru.practicum.ewm.stats.client.client.StatsClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hibernate.type.LocalTimeType.FORMATTER;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final LocationRepository locationRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestRepository participationRequestRepository;

    private final UserService userService;
    private final CategoryService categoryService;

    private final StatsClient statsClient;

    private static final String APPLICATION = "ewm-main-service";

    @Override
    @Transactional
    public Event add(Long userId, NewEventDto newEventDto) {
        User initiator = userService.getById(userId);
        Category category = categoryService.getById(newEventDto.getCategory());

        Location location = new Location(newEventDto.getLocation());
        locationRepository.save(location);

        Event event = EventMapper.toEntity(newEventDto);
        event.setInitiator(initiator);
        event.setCategory(category);
        event.setLocation(location);
        event.setState(EventState.PENDING);
        event.setCreatedOn(LocalDateTime.now());

        return eventRepository.save(event);
    }

    @Override
    @Transactional
    public Event updateEventByInitiator(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event event = getEventByInitiator(userId, eventId);

        if (event.getState() == EventState.PUBLISHED) {
            throw new ValidationException("Cannot update published event", HttpStatus.CONFLICT);
        }

        updateEventFields(event, updateEventUserRequest);

        if (updateEventUserRequest.getUpdateStateAction() != null) {
            if (updateEventUserRequest.getUpdateStateAction() == UpdateStateAction.SEND_TO_REVIEW) {
                event.setState(EventState.PENDING);
            } else if (updateEventUserRequest.getUpdateStateAction() == UpdateStateAction.CANCEL_REVIEW) {
                event.setState(EventState.CANCELED);
            }
        }

        return eventRepository.save(event);
    }

    @Override
    @Transactional
    public Event updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = getEventById(eventId);

        if (updateEventAdminRequest.getUpdateStateAction() != null) {
            if (updateEventAdminRequest.getUpdateStateAction() == NewStateAction.PUBLISH_EVENT) {
                if (event.getState() != EventState.PENDING) {
                    throw new ValidationException("Cannot publish the event because it's not in the right state: " + event.getState(), HttpStatus.CONFLICT);
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (updateEventAdminRequest.getUpdateStateAction() == NewStateAction.REJECT_EVENT) {
                if (event.getState() == EventState.PUBLISHED) {
                    throw new ValidationException("Cannot reject the event because it's already published", HttpStatus.CONFLICT);
                }
                event.setState(EventState.CANCELED);
            }
        }

        updateEventFields(event, updateEventAdminRequest);

        return eventRepository.save(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getEventsForAdmin(List<Long> users, List<String> states, List<Long> categories,
                                         String rangeStart, String rangeEnd, int from, int size, String remoteAddr) {
        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        LocalDateTime start = parseDate(rangeStart);
        LocalDateTime end = parseDate(rangeEnd);

        List<EventState> eventStates = states != null ?
                states.stream().map(EventState::valueOf).collect(Collectors.toList()) : null;

        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "eventDate"));

        List<Event> events = eventRepository.findAllByCriteria(users, eventStates, categories, start, end, pageRequest);

        return events.isEmpty() ? Collections.emptyList() : events;
    }

    @Override
    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id=" + eventId + " was not found"));
    }

    @Override
    public List<Event> getEventsByInitiator(Long userId, int from, int size) {
        User initiator = userService.getById(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return eventRepository.findByInitiator(initiator, pageRequest);
    }

    @Override
    public Event getEventByInitiator(Long userId, Long eventId) {
        User initiator = userService.getById(userId);
        return eventRepository.findByIdAndInitiator(eventId, initiator)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getEventsPublic(String text, List<Long> categories, Boolean paid,
                                       LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                       Boolean onlyAvailable,
                                       int from, int size, String ip) {

        PageRequest  pageRequest = PageRequest.of(from / size, size);

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
            rangeEnd = LocalDateTime.now().plusYears(1);
        }

        List<Event> events = eventRepository.findPublishedEvents(text, categories, paid, rangeStart, rangeEnd, pageRequest);

        if (onlyAvailable) {
            events = events.stream()
                    .filter(event -> event.getParticipantLimit() == 0 || event.getConfirmedRequests() < event.getParticipantLimit())
                    .collect(Collectors.toList());
        }

        saveStats("/events", ip);

        return events;
    }

    @Override
    @Transactional(readOnly = true)
    public Event getPublishedEvent(Long id, String ip) {
        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new EntityNotFoundException("Event with id=" + id + " was not found or is not published"));

        saveStats("/events/" + id, ip);

        return event;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequest> getEventParticipationRequests(Long userId, Long eventId) {
        Event event = getEventByInitiator(userId, eventId);
        return participationRequestRepository.findAllByEventId(eventId);
    }

    @Transactional
    public EventRequestStatusUpdateResult updateEventRequestsStatus(Long userId, Long eventId,
                                                                    EventRequestStatusUpdateRequest updateRequest) {
        Event event = getEventByInitiator(userId, eventId);

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ValidationException("The event does not require request moderation", HttpStatus.BAD_REQUEST);
        }

        List<ParticipationRequest> requests = participationRequestRepository.findAllById(updateRequest.getRequestIds());

        if (requests.size() != updateRequest.getRequestIds().size()) {
            throw new EntityNotFoundException("Some of the request ids are invalid");
        }

        long confirmedRequests = participationRequestRepository.countByEventIdAndStatus(eventId, ParticipationRequestState.CONFIRMED);
        int availableSlots = event.getParticipantLimit() - (int) confirmedRequests;

        if (updateRequest.getStatus() == ParticipationRequestState.CONFIRMED && availableSlots < updateRequest.getRequestIds().size()) {
            throw new ValidationException("The participant limit has been reached", HttpStatus.BAD_REQUEST);
        }

        List<ParticipationRequest> confirmedList = new ArrayList<>();
        List<ParticipationRequest> rejectedList = new ArrayList<>();

        for (ParticipationRequest request : requests) {
            if (request.getStatus() != ParticipationRequestState.PENDING) {
                throw new ValidationException("Can only update pending requests", HttpStatus.BAD_REQUEST);
            }
            request.setStatus(updateRequest.getStatus());
            if (updateRequest.getStatus() == ParticipationRequestState.CONFIRMED) {
                confirmedList.add(request);
            } else {
                rejectedList.add(request);
            }
        }

        participationRequestRepository.saveAll(requests);

        return new EventRequestStatusUpdateResult(
                ParticipationRequestMapper.toDtoList(confirmedList),
                ParticipationRequestMapper.toDtoList(rejectedList)
        );
    }

    private void updateEventFields(Event event, UpdateEventUserRequest updateRequest) {
        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }
        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }
        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getCategory() != null) {
            Category category = categoryService.getById(updateRequest.getCategory());
            event.setCategory(category);
        }
        if (updateRequest.getEventDate() != null) {
            event.setEventDate(updateRequest.getEventDate());
        }
        if (updateRequest.getLocation() != null) {
            event.setLocation(updateRequest.getLocation());
        }
        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }
        if (updateRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }
        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }
    }

    private void updateEventFields(Event event, UpdateEventAdminRequest updateRequest) {
        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }
        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }
        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getCategory() != null) {
            Category category = categoryService.getById(updateRequest.getCategory());
            event.setCategory(category);
        }
        if (updateRequest.getEventDate() != null) {
            event.setEventDate(updateRequest.getEventDate());
        }
        if (updateRequest.getLocation() != null) {
            event.setLocation(updateRequest.getLocation());
        }
        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }
        if (updateRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }
        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }
    }

    /**
     * Сохранение статистики по посещениям
     *
     * @param uri           the uri
     * @param remoteAddress the remote address
     */
    private void saveStats(String uri, String remoteAddress) {
        statsClient.saveStats(new EndpointHitDto(null, APPLICATION, uri, remoteAddress,
                LocalDateTime.now().format(FORMATTER)));
    }

    private LocalDateTime parseDate(String date) {
        return date != null ? LocalDateTime.parse(date, FORMATTER) : null;
    }

}