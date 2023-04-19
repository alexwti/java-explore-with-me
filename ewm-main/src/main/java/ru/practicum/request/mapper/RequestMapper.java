package ru.practicum.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.request.dto.RequestsDto;
import ru.practicum.request.model.Request;

@Mapper(componentModel = "spring")
@Component
public interface RequestMapper {
    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    RequestsDto toRequestDto(Request request);
}
