package ru.practicum.stats.service.service;

import ru.practicum.dto.StatsDto;

import java.util.List;

public interface StatsService {

    void save(StatsDto statsDto);

    List<StatsDto> getStats(String start, String end, List<String> uris, Boolean unique);

}
