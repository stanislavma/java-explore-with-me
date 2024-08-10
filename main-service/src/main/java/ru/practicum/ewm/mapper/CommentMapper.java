package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.model.Comment;

public class CommentMapper {

    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .eventId(comment.getEvent().getId())
                .author(UserMapper.toShortDto(comment.getAuthor()))
                .text(comment.getText())
                .created(comment.getCreated())
                .state(comment.getState())
                .build();
    }

}