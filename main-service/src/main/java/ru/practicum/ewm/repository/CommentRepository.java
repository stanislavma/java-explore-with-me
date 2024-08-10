package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.enums.CommentState;
import ru.practicum.ewm.model.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByEventIdAndStateOrderByCreatedDesc(Long eventId, CommentState state);

    Optional<Comment> findByIdAndAuthorId(Long id, Long authorId);

}