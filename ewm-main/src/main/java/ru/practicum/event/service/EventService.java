package ru.practicum.event.service;


import ru.practicum.enums.EventState;
import ru.practicum.enums.SortValue;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventUpdateRequestDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getEvents(Long userId, Integer from, Integer size);

    EventFullDto updateEventByAdmin(Long eventId, EventUpdateRequestDto eventUpdateRequestDto);

    EventFullDto updateEventByUser(Long userId, Long eventId, EventUpdateRequestDto eventUpdateRequestDto);

    EventFullDto getEventByUser(Long userId, Long eventId);

    List<EventFullDto> findEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categoriesId,
                                         String rangeStart, String rangeEnd, Integer from, Integer size);

    List<EventFullDto> findEventsByUser(String text, List<Long> categories, Boolean paid, String rangeStart,
                                        String rangeEnd, Boolean onlyAvailable, SortValue sort, Integer from, Integer size, HttpServletRequest request);

    EventFullDto getEvent(Long id, HttpServletRequest request);

    void setView(Event event);

    void setView(List<Event> events);
}
