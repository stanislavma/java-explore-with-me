package ru.practicum.stats.service.mapper;

import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stats.service.model.EndpointHit;

public class ViewStatsDtoMapper {

    public static ViewStatsDto toDto(EndpointHit endpointHit) {

        return new ViewStatsDto(
                endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getId()
        );

    }

}
