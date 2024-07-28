package ru.practicum.stats.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stats.service.StatsRepository;
import ru.practicum.stats.service.common.DateTimeConverter;
import ru.practicum.stats.service.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public void save(EndpointHit endpointHit) {
        statsRepository.save(endpointHit);
    }

    @Override
    public List<EndpointHit> getStats(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime startTime = DateTimeConverter.parseDateTime(start);
        LocalDateTime endTime = DateTimeConverter.parseDateTime(end);

        if (unique) {
            return statsRepository.findUniqueStats(startTime, endTime, uris);
        } else {
            return statsRepository.findStats(startTime, endTime, uris);
        }

    }

}
