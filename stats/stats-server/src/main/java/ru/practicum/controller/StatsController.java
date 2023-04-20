package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CreateEndpointHitDto;
import ru.practicum.dto.EndPointHitDto;
import ru.practicum.model.EndPointHit;
import ru.practicum.model.ViewStats;
import ru.practicum.service.StatsService;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndPointHitDto createHit(@RequestBody @Validated CreateEndpointHitDto createEndpointHitDto) {
        return statsService.createHit(createEndpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique) {
        List<ViewStats> list = statsService.getStats(start, end, uris, unique);
        return list;
        //return statsService.getStats(start, end, uris, unique);
    }
    @GetMapping("/allstats")
    public List<EndPointHit> getAllStats(){
        return statsService.getAllStats();
    }


}
