package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.enums.*;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.*;
import ru.practicum.ewm.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentDto add(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        Event event = eventRepository.findByIdWithDetails(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие не найдено"));

        validateCommentCreation(event);

        Comment comment = Comment.builder()
                .author(user)
                .event(event)
                .text(newCommentDto.getText())
                .created(LocalDateTime.now())
                .state(CommentState.PENDING)
                .build();

        Comment savedComment = commentRepository.save(comment);
        log.info("Добавлен новый комментарий: {}", savedComment.getId());

        return CommentMapper.toDto(savedComment);
    }

    /**
     * Обновить комментарий
     * При обновлении комментария статус сбрасывается на "ожидает модерации"
     */
    @Override
    @Transactional
    public CommentDto update(Long userId, Long commentId, NewCommentDto updateCommentDto) {
        Comment comment = getCommentByIdAndAuthor(userId, commentId);

        comment.setText(updateCommentDto.getText());

        // Сбросить статус комментария на "ожидает модерации"
        comment.setState(CommentState.PENDING);

        Comment updatedComment = commentRepository.save(comment);
        log.info("Комментарий обновлен: {}", updatedComment.getId());

        return CommentMapper.toDto(updatedComment);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long commentId) {
        Comment comment = getCommentByIdAndAuthor(userId, commentId);

        commentRepository.delete(comment);
        log.info("Комментарий удален: {}", commentId);
    }

    /**
     * Получить комментарии к событию
     */
    @Override
    @Transactional
    public List<CommentDto> getEventComments(Long eventId) {
        List<Comment> comments = commentRepository.findByEventIdAndStateOrderByCreatedDesc(eventId, CommentState.APPROVED);
        return comments.stream().map(CommentMapper::toDto).collect(Collectors.toList());
    }

    /**
     * Модерация комментария
     */
    @Override
    @Transactional
    public CommentDto moderate(Long commentId, CommentState newState) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий не найден по id: " + commentId));

        comment.setState(newState);
        Comment moderatedComment = commentRepository.save(comment);
        log.info("Комментарий {} промедерирован и установлен статус: {}", commentId, newState);

        return CommentMapper.toDto(moderatedComment);
    }

    private Comment getCommentByIdAndAuthor(Long userId, Long commentId) {
        return commentRepository.findByIdAndAuthorId(commentId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий не найден"));
    }

    private void validateCommentCreation(Event event) {
        if (event.getState() != EventState.PUBLISHED) {
            throw new ValidationException("Комментарии могут быть оставлены только к опубликованным событиям",
                    HttpStatus.BAD_REQUEST);
        }
    }

}