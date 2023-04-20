package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.dto.EndPointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.enums.AdminStateAction;
import ru.practicum.enums.EventState;
import ru.practicum.enums.SortValue;
import ru.practicum.enums.UserStateAction;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventUpdateRequestDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final UserRepository userRepository;
    private final StatsClient statsClient;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional
    @Override
    public EventFullDto createEvent(Long userId, EventNewDto eventNewDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id = %s not found", userId)));
        Category category = categoryRepository.findById(eventNewDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category not found"));
        if (eventNewDto.getEventDate().isBefore(LocalDateTime.now())) {
            throw new ConflictException(String.format("Wrong data: event date must be in the past: %s", eventNewDto.getEventDate()));
        }
        Event event = eventMapper.toEventModel(eventNewDto);
        event.setCategory(category);
        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.now());
        event.setConfirmedRequests(0L);
        event.setEventState(EventState.PENDING);
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getEvents(Long userId, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        return eventRepository.findAllByInitiatorId(userId, page).toList().stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto updateEventByAdmin(Long eventId, EventUpdateRequestDto eventUpdateRequestDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id %s not found", eventId)));

        if (eventUpdateRequestDto.getEventDate() != null) {
            if (LocalDateTime.parse(eventUpdateRequestDto.getEventDate(),
                    dateTimeFormatter).isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ConflictException(String.format("Wrong data: event starts in less then 2 hours: %s",
                        eventUpdateRequestDto.getEventDate()));
            } else {
                event.setEventDate(LocalDateTime.parse(eventUpdateRequestDto.getEventDate(),
                        dateTimeFormatter));
            }
        }

        if (eventUpdateRequestDto.getCategory() != null) {
            Category category = categoryRepository.findById(eventUpdateRequestDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category not found for update"));
            event.setCategory(category);
        }
        if (eventUpdateRequestDto.getAnnotation() != null) {
            event.setAnnotation(eventUpdateRequestDto.getAnnotation());
        }
        if (eventUpdateRequestDto.getDescription() != null) {
            event.setDescription(eventUpdateRequestDto.getDescription());
        }
        if (eventUpdateRequestDto.getLocation() != null) {
            event.setLocation(eventUpdateRequestDto.getLocation());
        }
        if (eventUpdateRequestDto.getPaid() != null) {
            event.setPaid(eventUpdateRequestDto.getPaid());
        }
        if (eventUpdateRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUpdateRequestDto.getParticipantLimit().intValue());
        }
        if (eventUpdateRequestDto.getRequestModeration() != null) {
            event.setRequestModeration(eventUpdateRequestDto.getRequestModeration());
        }
        if (eventUpdateRequestDto.getTitle() != null) {
            event.setTitle(eventUpdateRequestDto.getTitle());
        }

        if (eventUpdateRequestDto.getStateAction() != null) {
            if (eventUpdateRequestDto.getStateAction().equalsIgnoreCase(AdminStateAction.PUBLISH_EVENT.name())) {
                if (event.getEventState() == EventState.PUBLISHED) {
                    throw new ConflictException("Event already published");
                }
                if (event.getEventState().equals(EventState.CANCELED)) {
                    throw new ConflictException("Event already canceled");
                }
                event.setEventState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (eventUpdateRequestDto.getStateAction().equalsIgnoreCase(AdminStateAction.REJECT_EVENT.name())) {
                if (event.getEventState() == EventState.PUBLISHED) {
                    throw new ConflictException("Event already published");
                }
                event.setEventState(EventState.CANCELED);
            }
        }
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Transactional
    @Override
    public EventFullDto updateEventByUser(Long userId, Long eventId, EventUpdateRequestDto eventUpdateRequestDto) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id %s not found", eventId)));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("You can't update this event");
        }

        if (eventUpdateRequestDto.getEventDate() != null) {
            if (LocalDateTime.parse(eventUpdateRequestDto.getEventDate(),
                    dateTimeFormatter).isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ConflictException(String.format("Wrong data: event starts in less then 2 hours: %s",
                        eventUpdateRequestDto.getEventDate()));
            } else {
                event.setEventDate(LocalDateTime.parse(eventUpdateRequestDto.getEventDate(),
                        dateTimeFormatter));
            }
        }

        if (eventUpdateRequestDto.getCategory() != null) {
            Category category = categoryRepository.findById(eventUpdateRequestDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category not found for update"));
            event.setCategory(category);
        }

        if (event.getEventState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("You can't update published event");
        }

        if (eventUpdateRequestDto.getAnnotation() != null) {
            event.setAnnotation(eventUpdateRequestDto.getAnnotation());
        }
        if (eventUpdateRequestDto.getDescription() != null) {
            event.setDescription(eventUpdateRequestDto.getDescription());
        }
        if (eventUpdateRequestDto.getLocation() != null) {
            event.setLocation(eventUpdateRequestDto.getLocation());
        }
        if (eventUpdateRequestDto.getPaid() != null) {
            event.setPaid(eventUpdateRequestDto.getPaid());
        }
        if (eventUpdateRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUpdateRequestDto.getParticipantLimit().intValue());
        }
        if (eventUpdateRequestDto.getRequestModeration() != null) {
            event.setRequestModeration(eventUpdateRequestDto.getRequestModeration());
        }
        if (eventUpdateRequestDto.getTitle() != null) {
            event.setTitle(eventUpdateRequestDto.getTitle());
        }

        if (eventUpdateRequestDto.getStateAction() != null) {
            if (eventUpdateRequestDto.getStateAction().equalsIgnoreCase(UserStateAction.SEND_TO_REVIEW.name())) {
                event.setEventState(EventState.PENDING);
            } else {
                event.setEventState(EventState.CANCELED);
            }
        }
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEventByUser(Long userId, Long eventId) {
        return eventMapper.toEventFullDto(eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Events not found")));
    }

    public List<EventFullDto> findEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categoriesId,
                                                String rangeStart, String rangeEnd, Integer from, Integer size) {

        List<EventFullDto> result = new ArrayList<>();
        List<Event> events = eventRepository.findEventsByAdmin(users, states, categoriesId, rangeStart, rangeEnd,
                from, size);

        if (events != null) {
            getViewsForEvents(events);
            result = events.stream()
                    .map(eventMapper::toEventFullDto)
                    .collect(Collectors.toList());
        }
        return result;
    }

    public List<EventFullDto> findEventsByUser(String text, List<Long> categories, Boolean paid, String rangeStart,
                                               String rangeEnd, Boolean onlyAvailable, SortValue sort, Integer from,
                                               Integer size, HttpServletRequest request) {
        List<EventFullDto> result = new ArrayList<>();
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (rangeStart != null) {
            start = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (rangeStart != null) {
            end = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (text == null) text = "";
        PageRequest pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findByParamsOrderByDate(text.toLowerCase(), List.of(EventState.PUBLISHED),
                categories, paid, start, end, onlyAvailable, pageable);
        getViewsForEvents(events);

        if (sort != null && sort.equals(SortValue.VIEWS)) {
            events = events.stream().sorted(Comparator.comparing(Event::getViews)).collect(Collectors.toList());
        }
        sendStat(events, request);
        if (events != null) {
            result = events.stream()
                    .map(eventMapper::toEventFullDto)
                    .collect(Collectors.toList());
        }
        return result;
    }

    public void sendStat(List<Event> events, HttpServletRequest request) {
        LocalDateTime now = LocalDateTime.now();
        String remoteAddr = request.getRemoteAddr();
        String nameService = "ewm-main-service";

        EndPointHitDto requestDto = new EndPointHitDto();
        requestDto.setTimestamp(now.format(dateTimeFormatter));
        requestDto.setUri("/events");
        requestDto.setApp(nameService);
        requestDto.setIp(request.getRemoteAddr());
        statsClient.addStats(requestDto);
        for (Event event : events) {
            EndPointHitDto requestDtoEv = new EndPointHitDto();
            requestDtoEv.setTimestamp(now.format(dateTimeFormatter));
            requestDtoEv.setUri("/events/" + event.getId());
            requestDtoEv.setApp(nameService);
            requestDtoEv.setIp(remoteAddr);
            statsClient.addStats(requestDto);
        }
    }

    public void getViewsForEvents(List<Event> events) {
        if (events.size() > 0) {
            LocalDateTime start = events.get(0).getCreatedOn();
            List<String> uris = new ArrayList<>();
            Map<String, Event> eventsUri = new HashMap<>();
            String uri = "";
            for (Event event : events) {
                if (start.isBefore(event.getCreatedOn())) {
                    start = event.getCreatedOn();
                }
                uri = "/events/" + event.getId();
                uris.add(uri);
                eventsUri.put(uri, event);
                event.setViews(0L);
            }

            String startTime = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            List<ViewStatsDto> stats = getStats(startTime, endTime, uris);
            stats.forEach((stat) ->
                    eventsUri.get(stat.getUri()).setViews(stat.getHits()));
        }
    }

    @Override
    public void setView(Event event) {
        String startTime = event.getCreatedOn().format(dateTimeFormatter);
        String endTime = LocalDateTime.now().format(dateTimeFormatter);
        List<String> uris = List.of("/events/" + event.getId());

        List<ViewStatsDto> stats = getStats(startTime, endTime, uris);
        if (stats.size() == 1) {
            event.setViews(stats.get(0).getHits());
        } else {
            event.setViews(0L);
        }
    }

    public void setView(List<Event> events) {
        LocalDateTime start = events.get(0).getCreatedOn();
        List<String> uris = new ArrayList<>();
        Map<String, Event> eventsUri = new HashMap<>();
        String uri = "";
        for (Event event : events) {
            if (start.isBefore(event.getCreatedOn())) {
                start = event.getCreatedOn();
            }
            uri = "/events/" + event.getId();
            uris.add(uri);
            eventsUri.put(uri, event);
            event.setViews(0L);
        }

        String startTime = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        List<ViewStatsDto> stats = getStats(startTime, endTime, uris);
        stats.forEach((stat) ->
                eventsUri.get(stat.getUri()).setViews(stat.getHits()));
    }


    private List<ViewStatsDto> getStats(String startTime, String endTime, List<String> uris) {
        return statsClient.getStats(startTime, endTime, uris, false);
    }

    @Override
    public EventFullDto getEvent(Long id, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndPublishedOnIsNotNull(id)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id  %s not found", id)));
        setView(event);
        sendStat(Arrays.asList(event), request);
        return eventMapper.toEventFullDto(event);
    }
}
