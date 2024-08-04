package ru.practicum.ewm.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.common.DateTimeConverter;
import ru.practicum.ewm.stats.repository.StatsRepository;
import ru.practicum.ewm.stats.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
            List<String> uriListCleaned = cleanList(uriList);
            if (unique) {
                endpointHits = statsRepository.findUniqueStats(startTime, endTime, uriListCleaned);
            } else {
                endpointHits = statsRepository.findStats(startTime, endTime, uriListCleaned);
            }
        }

        log.info("stats records - {}", endpointHits.size());

        return endpointHits;
    }

    public static List<String> cleanList(List<String> uriList) {
        return uriList.stream()
                .map(s -> s.replace("[", "").replace("]", "").trim())
                .collect(Collectors.toList());
    }

}
