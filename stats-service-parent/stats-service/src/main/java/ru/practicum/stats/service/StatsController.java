package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.StatsDto;
import ru.practicum.stats.service.service.StatsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stats")
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<Void> saveStats(@RequestBody StatsDto statsDto) {
        statsService.save(statsDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<List<StatsDto>> getStats(@RequestParam String start,
                                                   @RequestParam String end,
                                                   @RequestParam List<String> uris,
                                                   @RequestParam Boolean unique) {
        List<StatsDto> stats = statsService.getStats(start, end, uris, unique);
        return ResponseEntity.ok(stats);
    }
}
