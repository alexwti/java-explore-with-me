package ru.practicum.service;

import ru.practicum.dto.CreateEndpointHitDto;
import ru.practicum.dto.EndPointHitDto;
import ru.practicum.model.ViewStats;

import java.util.List;

public interface StatsService {
    EndPointHitDto createHit(CreateEndpointHitDto createEndpointHitDto);

    List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique);
}
