package ru.practicum.ewm.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.controller.CalculatedData;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationRequest;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.service.CompilationService;
import ru.practicum.ewm.service.impl.RequestService;
import ru.practicum.ewm.stats.client.client.StatsClient;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Контроллер для администратора подборок
 */
@RestController
@RequestMapping("/admin/compilations")
public class CompilationAdminController extends CalculatedData {

    private final CompilationService compilationService;

    public CompilationAdminController(CompilationService compilationService,
                                      StatsClient statsClient, RequestService requestService) {
        super(statsClient, requestService);
        this.compilationService = compilationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
//    @Transactional
    public CompilationDto createCompilation(@RequestBody @Valid NewCompilationDto dto) {
        Compilation compilation = compilationService.createCompilation(dto.getTitle(), dto.getPinned(), dto.getEvents());

        Map<Long, Long> viewsMap = getViewsMap(new ArrayList<>(compilation.getEvents()));
        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(new ArrayList<>(compilation.getEvents()));

        return CompilationMapper.toDto(compilation, viewsMap, confirmedRequestsMap);
    }

    @PatchMapping("/{compId}")
//    @Transactional
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @RequestBody @Valid UpdateCompilationRequest dto) {
        Compilation compilation = compilationService.updateCompilation(
                compId, dto.getTitle(), dto.getPinned(), dto.getEvents());

        Map<Long, Long> viewsMap = getViewsMap(new ArrayList<>(compilation.getEvents()));
        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(new ArrayList<>(compilation.getEvents()));

        return CompilationMapper.toDto(compilation, viewsMap, confirmedRequestsMap);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilation(compId);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        Compilation compilation = compilationService.getCompilationById(compId);

        Map<Long, Long> viewsMap = getViewsMap(new ArrayList<>(compilation.getEvents()));
        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(new ArrayList<>(compilation.getEvents()));

        return CompilationMapper.toDto(compilation, viewsMap, confirmedRequestsMap);
    }

    @GetMapping
    public List<CompilationDto> getAllCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {

        List<Compilation> compilations = compilationService.getAllCompilations(pinned, from, size);

        return compilations.stream()
                .map(compilation -> {
                    Map<Long, Long> viewsMap = getViewsMap(new ArrayList<>(compilation.getEvents()));
                    Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(new ArrayList<>(compilation.getEvents()));
                    return CompilationMapper.toDto(compilation, viewsMap, confirmedRequestsMap);
                })
                .collect(Collectors.toList());
    }

}