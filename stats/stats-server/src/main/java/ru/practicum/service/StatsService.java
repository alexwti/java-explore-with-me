package ru.practicum.service;

import ru.practicum.dto.CreateEndpointHitDto;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.model.ViewStats;

import java.util.List;

public interface StatsService {
    EndpointHitDto createHit(CreateEndpointHitDto createEndpointHitDto);

    List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique);

}
