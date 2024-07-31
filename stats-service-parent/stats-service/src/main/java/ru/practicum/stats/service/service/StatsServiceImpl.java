package ru.practicum.stats.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.stats.service.repository.StatsRepository;
import ru.practicum.stats.service.common.DateTimeConverter;
import ru.practicum.stats.service.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public void save(EndpointHit endpointHit) {
        statsRepository.save(endpointHit);
        log.info("Saved endpoint hit: {}", endpointHit);
    }

    @Override
    public List<EndpointHit> getStats(String start, String end, List<String> uriList, Boolean unique) {
        LocalDateTime startTime = DateTimeConverter.parseDateTime(start);
        LocalDateTime endTime = DateTimeConverter.parseDateTime(end);

        List<EndpointHit> endpointHits;

        if (uriList == null || uriList.isEmpty()) {
            if (unique) {
                endpointHits = statsRepository.findUniqueStatsWithoutUris(startTime, endTime);
            } else {
                endpointHits = statsRepository.findStatsWithoutUris(startTime, endTime);
            }
        } else {
            if (unique) {
                endpointHits = statsRepository.findUniqueStats(startTime, endTime, uriList);
            } else {
                endpointHits = statsRepository.findStats(startTime, endTime, uriList);
            }
        }

        log.info("stats records - {}", endpointHits.size());

        return endpointHits;
    }

}
