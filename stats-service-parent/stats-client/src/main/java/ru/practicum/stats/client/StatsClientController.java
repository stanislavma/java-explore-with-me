package ru.practicum.stats.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class StatsClientController {

    private final StatsClient statsClient;

    @PostMapping("/hit")
    public ResponseEntity<EndpointHitDto> saveStats(@RequestBody EndpointHitDto endpointHitDto) {
        statsClient.saveStats(endpointHitDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(endpointHitDto);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStats(@RequestParam String start,
                                           @RequestParam String end,
                                           @RequestParam(required = false) List<String> uris,
                                           @RequestParam(defaultValue = "false") Boolean unique) {
        return statsClient.getStats(start, end, uris, unique);
    }

}