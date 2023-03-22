package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.CreateEndpointHitDto;
import ru.practicum.dto.ResponseEndpointHitDto;
import ru.practicum.model.EndPointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class StatsMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EndPointHit toEndpointHit(CreateEndpointHitDto createEndpointHitDto) {
        return new EndPointHit(null,
                createEndpointHitDto.getApp(),
                createEndpointHitDto.getUri(),
                createEndpointHitDto.getIp(),
                LocalDateTime.parse(createEndpointHitDto.getTimestamp(), FORMATTER));
    }

    public ResponseEndpointHitDto toResponseEndpointHitDto(EndPointHit endPointHit) {
        return new ResponseEndpointHitDto(
                endPointHit.getId(),
                endPointHit.getApp(),
                endPointHit.getUri(),
                endPointHit.getIp(),
                endPointHit.getTimestamp().format(FORMATTER)
        );
    }
}
