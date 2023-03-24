package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.CreateEndpointHitDto;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.model.EndPointHit;

@Mapper(componentModel = "spring")

public interface StatsMapper {
    @Mapping(source = "timestamp", target = "timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    EndPointHit toEndpointHit(CreateEndpointHitDto createEndpointHitDto);

    @Mapping(source = "timestamp", target = "timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    EndpointHitDto toResponseEndpointHitDto(EndPointHit endPointHit);
}
