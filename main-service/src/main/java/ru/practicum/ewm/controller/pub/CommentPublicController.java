package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.service.impl.CommentServiceImpl;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events/{eventId}/comments")
public class CommentPublicController {
    private final CommentServiceImpl commentService;

    @GetMapping
    public List<CommentDto> getEventComments(@PathVariable Long eventId) {
        log.info("Получение комментариев для мероприятия {}", eventId);
        return commentService.getEventComments(eventId);
    }

}