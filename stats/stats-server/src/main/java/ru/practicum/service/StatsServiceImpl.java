package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CreateEndpointHitDto;
import ru.practicum.dto.EndPointHitDto;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.ViewStats;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsRepository statsRepository;
    private final StatsMapper statsMapper;

    @Override
    public EndPointHitDto createHit(CreateEndpointHitDto createEndpointHitDto) {
        log.info("createEndpointHitDto: {}", createEndpointHitDto.toString());
        return statsMapper.toResponseEndpointHitDto(statsRepository.save(statsMapper.toEndpointHit(createEndpointHitDto)));
    }

    @Override
    public List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime parseStart = LocalDateTime.parse(start, FORMATTER);
        LocalDateTime parseEnd = LocalDateTime.parse(end, FORMATTER);
        if (unique) {
            return statsRepository.getStatsByDateUniqueIp(parseStart, parseEnd, uris);
        } else {
            return statsRepository.getStatsByDate(parseStart, parseEnd, uris);
        }
    }
}