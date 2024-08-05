package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.compilation.CompilationDto;
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

}