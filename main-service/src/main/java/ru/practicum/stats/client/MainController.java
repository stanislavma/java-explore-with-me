package ru.practicum.stats.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class MainController {

    private final StatsClient statsClient;

    @GetMapping("/")
    public ResponseEntity<?> getEvents(HttpServletRequest request) {
        EndpointHitDto endpointHitDto = new EndpointHitDto(
                null,
                "ewm-main-service",
                "/events/" ,
                request.getRemoteAddr(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
        statsClient.saveStats(endpointHitDto);

        return ResponseEntity.ok("Событие возвращено");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id, HttpServletRequest request) {
        EndpointHitDto endpointHitDto = new EndpointHitDto(
                null,
                "ewm-main-service",
                "/events/" + id,
                request.getRemoteAddr(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
        statsClient.saveStats(endpointHitDto);

        return ResponseEntity.ok("Событие возвращено");
    }

}
