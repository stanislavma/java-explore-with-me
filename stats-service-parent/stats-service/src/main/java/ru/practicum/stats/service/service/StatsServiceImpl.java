package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stats.service.model.Stats;
import ru.practicum.stats.service.StatsRepository;
import ru.practicum.stats.service.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    public void saveStats(Stats stats) {
        statsRepository.save(stats);
    }

    public List<Stats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        return statsRepository.findStats(start, end, uris, unique);
    }

}
