package ru.practicum.ewm.stats.mapper;

import ru.practicum.ewm.dto.stats.EndpointHitDto;
import ru.practicum.ewm.dto.stats.ViewStatsDto;
import ru.practicum.ewm.stats.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class EndpointHitDtoMapper {

    public static EndpointHit toEntity(EndpointHitDto dto) {
        if (dto == null) {
            return null;
        }

        return EndpointHit.builder()
                .id(dto.getId())
                .app(dto.getApp())
                .uri(dto.getUri())
                .ip(dto.getIp())
                .hitDate(LocalDateTime.parse(dto.getHitDate(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    public static ViewStatsDto toViewStatsDto(EndpointHit entity) {
        if (entity == null) {
            return null;
        }
        return new ViewStatsDto(entity.getApp(), entity.getUri(), entity.getHits());
    }

    public static List<ViewStatsDto> toViewStatsDtoList(List<EndpointHit> results) {
        return results.stream()
                .map(EndpointHitDtoMapper::toViewStatsDto)
                .collect(Collectors.toList());
    }

}