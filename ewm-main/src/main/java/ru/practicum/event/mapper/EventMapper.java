package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;

@Mapper(componentModel = "spring")
@Component
public interface EventMapper {
    @Mapping(target = "state", expression = "java(event.getEventState())")
    EventFullDto toEventFullDto(Event event);

    @Mapping(source = "category", target = "category.id")
    Event toEventModel(NewEventDto newEventDto);

    EventShortDto toEventShortDto(Event event);
}
