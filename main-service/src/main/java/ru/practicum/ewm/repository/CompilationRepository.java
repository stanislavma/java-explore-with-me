package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.Compilation;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query("SELECT DISTINCT c FROM Compilation c " +
            "LEFT JOIN FETCH c.events e " +
            "LEFT JOIN FETCH e.initiator " +
            "LEFT JOIN FETCH e.category " +
            "WHERE c.pinned = :pinned")
    List<Compilation> findAllByPinnedWithEvents(@Param("pinned") Boolean pinned, Pageable pageable);

    @Query("SELECT DISTINCT c FROM Compilation c " +
            "LEFT JOIN FETCH c.events e " +
            "LEFT JOIN FETCH e.initiator " +
            "LEFT JOIN FETCH e.category " +
            "WHERE c.id = :id")
    Optional<Compilation> findByIdWithEvents(@Param("id") Long id);

    @Query("SELECT DISTINCT c FROM Compilation c " +
            "LEFT JOIN FETCH c.events e " +
            "LEFT JOIN FETCH e.initiator " +
            "LEFT JOIN FETCH e.category ")
    List<Compilation> findAllWithEvents(Pageable pageable);

}