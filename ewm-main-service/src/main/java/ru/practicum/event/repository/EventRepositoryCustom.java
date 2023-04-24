package ru.practicum.event.repository;


import ru.practicum.enums.EventState;
import ru.practicum.event.model.Event;

import java.util.List;

public interface EventRepositoryCustom {
    List<Event> findEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categoriesId,
                                  String rangeStart, String rangeEnd, Integer from, Integer size);
}
