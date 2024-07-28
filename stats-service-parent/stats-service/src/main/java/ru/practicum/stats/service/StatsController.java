package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stats.service.mapper.EndpointHitDtoMapper;
import ru.practicum.stats.service.model.EndpointHit;
import ru.practicum.stats.service.service.StatsService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stats")
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<Void> saveStats(@RequestBody EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = EndpointHitDtoMapper.toEntity(endpointHitDto);
        statsService.save(endpointHit);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<ViewStatsDto>> getStats(@RequestParam String start,
                                                       @RequestParam String end,
                                                       @RequestParam(required = false) List<String> uris,
                                                       @RequestParam(defaultValue = "false") Boolean unique) {

        List<EndpointHit> endpointHits = statsService.getStats(start, end, uris, unique);

        List<ViewStatsDto> viewStatsDtoList = endpointHits.stream()
                .map(endpointHit -> EndpointHitDtoMapper
                        .toViewStatsDto(endpointHit.getApp(), endpointHit.getUri(), endpointHit.getHits()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(viewStatsDtoList);
    }
}
