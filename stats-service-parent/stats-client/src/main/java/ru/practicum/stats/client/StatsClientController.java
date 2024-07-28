package ru.practicum.stats.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.stats.client.StatsClient;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stats")
public class StatsClientController {

    private final StatsClient statsClient;

    @PostMapping("/hit")
    public ResponseEntity<Void> saveStats(@RequestBody EndpointHitDto EndpointHitDto) {
        statsClient.saveStats(EndpointHitDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStats(@RequestParam String start,
                                           @RequestParam String end,
                                           @RequestParam List<String> uris,
                                           @RequestParam Boolean unique) {
        return statsClient.getStats(start, end, uris, unique);
    }

}