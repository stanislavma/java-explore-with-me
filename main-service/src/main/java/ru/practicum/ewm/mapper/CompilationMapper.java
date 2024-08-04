package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationRequest;
import ru.practicum.ewm.model.Compilation;

import java.util.Map;
import java.util.stream.Collectors;

public class CompilationMapper {

    public static CompilationDto toDto(Compilation compilation,
                                       Map<Long, Long> viewsMap,
                                       Map<Long, Long> confirmedRequestsMap) {
        return new CompilationDto(
                compilation.getId(),
                compilation.getTitle(),
                compilation.getPinned(),
                compilation.getEvents().stream()
                        .map(event -> EventMapper.toShortDto(
                                event,
                                viewsMap.getOrDefault(event.getId(), 0L),
                                confirmedRequestsMap.getOrDefault(event.getId(), 0L)))
                        .collect(Collectors.toSet())
        );
    }

    public static Compilation toEntity(NewCompilationDto dto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(dto.getTitle());
        compilation.setPinned(dto.getPinned() != null ? dto.getPinned() : false);
        return compilation;
    }

    public static void updateCompilationFromDto(Compilation compilation, UpdateCompilationRequest dto) {
        if (dto.getTitle() != null) {
            compilation.setTitle(dto.getTitle());
        }

        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }
    }

}