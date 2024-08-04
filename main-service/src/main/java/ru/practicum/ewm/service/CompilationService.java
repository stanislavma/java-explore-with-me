package ru.practicum.ewm.service;

import ru.practicum.ewm.model.Compilation;

import java.util.List;
import java.util.Set;

public interface CompilationService {

    Compilation createCompilation(String title, Boolean pinned, Set<Long> eventIds);

    void deleteCompilation(Long compId);

    Compilation updateCompilation(Long compId, String title, Boolean pinned, Set<Long> eventIds);

    Compilation getCompilationById(Long compId);

    List<Compilation> getAllCompilations(Boolean pinned, int from, int size);

    List<Compilation> getCompilations(Boolean pinned, int from, int size);


}