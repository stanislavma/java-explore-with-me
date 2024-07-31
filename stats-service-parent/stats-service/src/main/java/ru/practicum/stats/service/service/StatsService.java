package ru.practicum.stats.service.service;

import ru.practicum.stats.service.model.EndpointHit;

import java.util.List;

public interface StatsService {

    void save(EndpointHit endpointHit);

    List<EndpointHit> getStats(String start, String end, List<String> uris, Boolean unique);

}
