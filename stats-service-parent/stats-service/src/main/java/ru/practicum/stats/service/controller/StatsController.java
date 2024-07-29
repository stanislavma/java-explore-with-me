package ru.practicum.stats.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stats.service.mapper.EndpointHitDtoMapper;
import ru.practicum.stats.service.model.EndpointHit;
import ru.practicum.stats.service.service.StatsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<EndpointHit> saveStats(@RequestBody EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = EndpointHitDtoMapper.toEntity(endpointHitDto);
        statsService.save(endpointHit);
        return ResponseEntity.status(HttpStatus.CREATED).body(endpointHit);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStatsDto>> getStats(@RequestParam String start,
                                                       @RequestParam String end,
                                                       @RequestParam(required = false) List<String> uris,
                                                       @RequestParam(defaultValue = "false") Boolean unique) {

        List<EndpointHit> endpointHits = statsService.getStats(start, end, uris, unique);

        List<ViewStatsDto> viewStatsDtoList = EndpointHitDtoMapper.toViewStatsDtoList(endpointHits);

        return ResponseEntity.ok(viewStatsDtoList);
    }

}
