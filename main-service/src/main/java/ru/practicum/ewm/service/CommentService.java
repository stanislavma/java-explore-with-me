package ru.practicum.ewm.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.enums.CommentState;

import java.util.List;

public interface CommentService {

    @Transactional
    CommentDto add(Long userId, Long eventId, NewCommentDto newCommentDto);

    @Transactional
    CommentDto update(Long userId, Long commentId, NewCommentDto updateCommentDto);

    @Transactional
    void delete(Long userId, Long commentId);

    List<CommentDto> getEventComments(Long eventId);

    @Transactional
    CommentDto moderate(Long commentId, CommentState newState);

}
