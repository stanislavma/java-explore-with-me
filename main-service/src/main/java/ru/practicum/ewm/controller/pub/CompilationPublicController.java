package ru.practicum.ewm.controller.pub;

import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.controller.StatsData;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.service.CompilationService;
import ru.practicum.ewm.stats.client.client.StatsClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/compilations")
public class CompilationPublicController extends StatsData {

    private final CompilationService compilationService;

    public CompilationPublicController(StatsClient statsClient, CompilationService compilationService) {
        super(statsClient);
        this.compilationService = compilationService;
    }

    @GetMapping
    public List<CompilationDto> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        List<Compilation> compilations = compilationService.getCompilations(pinned, from, size);
        return compilations.stream()
                .map(compilation -> {
                    Map<Long, Long> viewsMap = getViewsMap((List<Event>) compilation.getEvents());
                    Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap((List<Event>) compilation.getEvents());
                    return CompilationMapper.toDto(compilation, viewsMap, confirmedRequestsMap);
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable Long compId) {
        Compilation compilation = compilationService.getCompilationById(compId);

        Map<Long, Long> viewsMap = getViewsMap((List<Event>) compilation.getEvents());
        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap((List<Event>) compilation.getEvents());

        return CompilationMapper.toDto(compilation, viewsMap, confirmedRequestsMap);
    }

}