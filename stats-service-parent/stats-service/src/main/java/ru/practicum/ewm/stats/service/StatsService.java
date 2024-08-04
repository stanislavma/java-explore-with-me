package ru.practicum.ewm.stats.service;

import ru.practicum.ewm.stats.model.EndpointHit;

import java.util.List;

public interface StatsService {

    void save(EndpointHit endpointHit);

    List<EndpointHit> getStats(String start, String end, List<String> uris, Boolean unique);

}
