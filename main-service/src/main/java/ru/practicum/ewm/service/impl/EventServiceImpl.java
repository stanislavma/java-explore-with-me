package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.event.UpdateEventAdminRequest;
import ru.practicum.ewm.dto.event.UpdateEventUserRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.stats.EndpointHitDto;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.enums.NewStateAction;
import ru.practicum.ewm.enums.RequestState;
import ru.practicum.ewm.enums.UpdateStateAction;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.LocationRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.service.CategoryService;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.UserService;
import ru.practicum.ewm.stats.client.client.StatsClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.common.Constants.DATE_TIME_FORMATTER;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final LocationRepository locationRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

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

        validateStatus(event);
        validateEventDateAfter2Hours(updateEventUserRequest);

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

        if (updateEventAdminRequest.getEventDate() != null) {
            // Время начала мероприятия должно быть позже даты публикации на час или больше
            validateEventStartDateAfterPublication(updateEventAdminRequest);
        }

        if (updateEventAdminRequest.getUpdateStateAction() != null) {
            if (updateEventAdminRequest.getUpdateStateAction() == NewStateAction.PUBLISH_EVENT) {
                // событие можно публиковать, только если оно в состоянии ожидания публикации
                validateIsPendingStatus(event);

                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (updateEventAdminRequest.getUpdateStateAction() == NewStateAction.REJECT_EVENT) {
                // событие можно отклонить, только если оно не опубликовано
                validateIsNotPublished(event);

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
            throw new IllegalArgumentException("Неверные параметры пагинации");
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
                .orElseThrow(() -> new EntityNotFoundException("Событие не найдено: " + eventId));
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
                .orElseThrow(() -> new EntityNotFoundException("Событие не найдено"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getEventsPublic(String text, List<Long> categories, Boolean paid,
                                       LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                       Boolean onlyAvailable,
                                       int from, int size, String ip) {
        validatePaging(from, size);
        validateMinTextLength(text);

        LocalDateTime now = LocalDateTime.now();
        rangeStart = getDefaultStartDateIfNull(rangeStart, now);
        rangeEnd = getDefaultEndDateIfNull(rangeEnd, now);
        validateDates(rangeStart, rangeEnd);

        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findPublishedEvents(text, categories, paid, rangeStart, rangeEnd, pageRequest);

        if (onlyAvailable) {
            events = events.stream()
                    .filter(event -> event.getParticipantLimit() == 0 ||
                            event.getConfirmedRequests() < event.getParticipantLimit())
                    .collect(Collectors.toList());
        }

        saveStats("/events", ip);

        return events;
    }

    private static LocalDateTime getDefaultEndDateIfNull(LocalDateTime rangeEnd, LocalDateTime now) {
        if (rangeEnd == null) {
            rangeEnd = now.plusYears(1);
        }
        return rangeEnd;
    }

    private static LocalDateTime getDefaultStartDateIfNull(LocalDateTime rangeStart, LocalDateTime now) {
        if (rangeStart == null) {
            rangeStart = now;
        }
        return rangeStart;
    }

    @Override
    @Transactional(readOnly = true)
    public Event getPublishedEvent(Long id, String ip) {
        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new EntityNotFoundException("Событие не найдено или не опубликовано"));

        saveStats("/events/" + id, ip);

        return event;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequest> getEventParticipationRequests(Long userId, Long eventId) {
        return requestRepository.findAllByEventId(eventId);
    }

    @Transactional
    public EventRequestStatusUpdateResult updateEventRequestsStatus(Long userId, Long eventId,
                                                                    EventRequestStatusUpdateRequest updateRequest) {
        Event event = getEventByInitiator(userId, eventId);

        int requestedSlotsCount = updateRequest.getRequestIds().size();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ValidationException("Мероприятие не требует модерации запроса", HttpStatus.BAD_REQUEST);
        }

        List<ParticipationRequest> requests = requestRepository.findAllById(updateRequest.getRequestIds());

        if (requests.size() != requestedSlotsCount) {
            throw new EntityNotFoundException("Некоторые id запросов не существуют");
        }

        long confirmedRequestsCount = requestRepository.countByEventIdAndStatus(eventId, RequestState.CONFIRMED);
        int availableSlotsCount = event.getParticipantLimit() - (int) confirmedRequestsCount;

        if (updateRequest.getStatus() == RequestState.CONFIRMED && availableSlotsCount < requestedSlotsCount) {
            throw new ValidationException("Достигнут лимит участников", HttpStatus.CONFLICT);
        }

        List<ParticipationRequest> confirmedList = new ArrayList<>();
        List<ParticipationRequest> rejectedList = new ArrayList<>();

        for (ParticipationRequest request : requests) {
            if (request.getStatus() != RequestState.PENDING) {
                throw new ValidationException("Обновить можно только ожидающие запросы", HttpStatus.CONFLICT);
            }
            request.setStatus(updateRequest.getStatus());
            if (updateRequest.getStatus() == RequestState.CONFIRMED) {
                confirmedList.add(request);
            } else {
                rejectedList.add(request);
            }
        }

        requestRepository.saveAll(requests);

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
                LocalDateTime.now().format(DATE_TIME_FORMATTER)));
    }

    private LocalDateTime parseDate(String date) {
        return date != null ? LocalDateTime.parse(date, DATE_TIME_FORMATTER) : null;
    }

    private static void validatePaging(int from, int size) {
        if (from < 0) {
            throw new ValidationException("Параметр From должен быть больше или равен нулю", HttpStatus.BAD_REQUEST);
        }

        if (size <= 0) {
            throw new ValidationException("Параметр size должен быть положительным.", HttpStatus.BAD_REQUEST);
        }
    }

    private static void validateMinTextLength(String text) {
        if (text != null && text.length() < 2) {
            throw new ValidationException("Текстовый запрос должен содержать не менее 2 символов",
                    HttpStatus.BAD_REQUEST);
        }
    }

    private static void validateEventDateAfter2Hours(UpdateEventUserRequest updateEventUserRequest) {
        if (updateEventUserRequest.getEventDate() != null) {
            LocalDateTime minEventDate = LocalDateTime.now().plusHours(2);
            if (updateEventUserRequest.getEventDate().isBefore(minEventDate)) {
                throw new ValidationException("Дата события должна быть не ранее," +
                        " чем через 2 часа.", HttpStatus.BAD_REQUEST);
            }
        }
    }

    private static void validateStatus(Event event) {
        if (event.getState() != EventState.PENDING && event.getState() != EventState.CANCELED) {
            throw new ValidationException("Изменить можно только запланированные " +
                    "или отмененные события.", HttpStatus.CONFLICT);
        }
    }

    private static void validateDates(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        LocalDateTime now = LocalDateTime.now();
        if (rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Дата начала должна быть раньше даты окончания.", HttpStatus.BAD_REQUEST);
        }
        if (rangeEnd.isBefore(now)) {
            throw new ValidationException("Дата окончания должна быть в будущем.", HttpStatus.BAD_REQUEST);
        }
    }

    private static void validateIsNotPublished(Event event) {
        if (event.getState() == EventState.PUBLISHED) {
            throw new ValidationException("Невозможно отклонить событие, так как оно уже опубликовано.",
                    HttpStatus.CONFLICT);
        }
    }

    private static void validateIsPendingStatus(Event event) {
        if (event.getState() != EventState.PENDING) {
            throw new ValidationException("Невозможно опубликовать событие, неверный статус : " +
                    event.getState(), HttpStatus.CONFLICT);
        }
    }

    private static void validateEventStartDateAfterPublication(UpdateEventAdminRequest updateEventAdminRequest) {
        if (updateEventAdminRequest.getEventDate() != null &&
                updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ValidationException("Время начала мероприятия должно быть позже " +
                    "даты публикации на час или больше", HttpStatus.BAD_REQUEST);
        }
    }

}