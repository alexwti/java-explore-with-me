package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/comments")
@RequiredArgsConstructor
@Validated
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@RequestBody @Valid NewCommentDto newCommentDto,
                                    @PathVariable(value = "userId") Long userId,
                                    @PathVariable(value = "eventId") Long eventId) {
        return commentService.createComment(newCommentDto, userId, eventId);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@RequestBody @Valid NewCommentDto newCommentDto,
                                    @PathVariable(value = "userId") Long userId,
                                    @PathVariable(value = "commentId") Long commentId) {
        return commentService.updateCommentByUser(newCommentDto, userId, commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByUser(@PathVariable(value = "userId") Long userId,
                                    @PathVariable(value = "commentId") Long commentId) {
        commentService.deleteCommentByUser(userId, commentId);
    }

    @GetMapping
    public List<CommentDto> getUserCommentsByCreateTime(@PathVariable(value = "userId") Long userId,
                                                        @PositiveOrZero
                                                        @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                        @Positive
                                                        @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                        @RequestParam(value = "createStart", required = false)
                                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                        LocalDateTime createStart,
                                                        @RequestParam(value = "createEnd", required = false)
                                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                        LocalDateTime createEnd) {
        return commentService.getUserCommentsByCreateTime(userId, createStart, createEnd, from, size);
    }
}
