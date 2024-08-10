package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.enums.CommentState;
import ru.practicum.ewm.service.impl.CommentServiceImpl;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentAdminController {
    private final CommentServiceImpl commentService;

    @PatchMapping("/{commentId}")
    public CommentDto moderateComment(@PathVariable Long commentId,
                                      @RequestParam CommentState state) {
        log.info("Moderate comment request received for comment {} with state {}", commentId, state);
        return commentService.moderate(commentId, state);
    }

}