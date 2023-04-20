package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.enums.EventState;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventUpdateRequestDto;
import ru.practicum.event.service.EventService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
public class AdminEventController {
    private final EventService eventService;

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable(name = "eventId") Long eventId, @Valid @RequestBody EventUpdateRequestDto eventUpdateRequestDto) {
        return eventService.updateEventByAdmin(eventId, eventUpdateRequestDto);

    }

    @GetMapping
    public List<EventFullDto> findEventsByAdmin(@RequestParam(name = "users", required = false) List<Long> users,
                                                @RequestParam(name = "states", required = false) List<EventState> states,
                                                @RequestParam(name = "categories", required = false) List<Long> categoriesId,
                                                @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                                @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                                @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                                @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        return eventService.findEventsByAdmin(users, states, categoriesId, rangeStart, rangeEnd, from, size);
    }
}
