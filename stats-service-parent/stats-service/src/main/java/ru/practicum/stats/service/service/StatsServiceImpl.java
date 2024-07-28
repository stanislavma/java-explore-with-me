package ru.practicum.stats.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stats.service.StatsRepository;
import ru.practicum.stats.service.common.DateTimeConverter;
import ru.practicum.stats.service.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public void save(EndpointHit endpointHit) {
        statsRepository.save(endpointHit);
    }

    @Override
    public List<EndpointHit> getStats(String start, String end, List<String> uriList, Boolean unique) {
        LocalDateTime startTime = DateTimeConverter.parseDateTime(start);
        LocalDateTime endTime = DateTimeConverter.parseDateTime(end);
        List<String> uriListCleaned = cleanList(uriList);

        if (unique) {
            return statsRepository.findUniqueStats(startTime, endTime, uriListCleaned);
        } else {
            return statsRepository.findStats(startTime, endTime, uriListCleaned);
        }

    }

    public static List<String> cleanList(List<String> uriList) {

        return uriList.stream()
                .map(s -> s.replace("[", "").replace("]", "").trim())
                .collect(Collectors.toList());
    }

}
