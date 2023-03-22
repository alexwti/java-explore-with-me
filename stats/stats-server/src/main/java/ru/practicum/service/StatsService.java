package ru.practicum.service;

import ru.practicum.dto.CreateEndpointHitDto;
import ru.practicum.dto.ResponseEndpointHitDto;
import ru.practicum.model.ViewStats;

import java.util.List;

public interface StatsService {
    ResponseEndpointHitDto createHit(CreateEndpointHitDto createEndpointHitDto);

    List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique);

}
