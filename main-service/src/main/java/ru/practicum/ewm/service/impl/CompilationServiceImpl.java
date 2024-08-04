package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.CompilationService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public Compilation createCompilation(String title, Boolean pinned, Set<Long> eventIds) {
        Compilation compilation = new Compilation();
        compilation.setTitle(title);
        compilation.setPinned(pinned != null ? pinned : false);
        if (eventIds != null && !eventIds.isEmpty()) {
            Set<Event> events = new HashSet<>(eventRepository.findAllById(eventIds));
            compilation.setEvents(events);
        }
        return compilationRepository.save(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new EntityNotFoundException("Compilation with id=" + compId + " was not found");
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public Compilation updateCompilation(Long compId, String title, Boolean pinned, Set<Long> eventIds) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Compilation with id=" + compId + " was not found"));

        if (title != null) {
            compilation.setTitle(title);
        }
        if (pinned != null) {
            compilation.setPinned(pinned);
        }
        if (eventIds != null) {
            Set<Event> events = new HashSet<>(eventRepository.findAllById(eventIds));
            compilation.setEvents(events);
        }

        return compilationRepository.save(compilation);
    }

    @Override
    public Compilation getCompilationById(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Compilation with id=" + compId + " was not found"));
    }

    @Override
    public List<Compilation> getAllCompilations(Boolean pinned, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        if (pinned != null) {
            return compilationRepository.findAllByPinned(pinned, pageRequest);
        } else {
            return compilationRepository.findAll(pageRequest).getContent();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Compilation> getCompilations(Boolean pinned, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        if (pinned != null) {
            return compilationRepository.findAllByPinned(pinned, pageRequest);
        } else {
            return compilationRepository.findAll(pageRequest).getContent();
        }
    }

}