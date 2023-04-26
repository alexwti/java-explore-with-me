package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {
    CommentDto createComment(NewCommentDto newCommentDto, Long userId, Long eventId);

    CommentDto updateCommentByUser(NewCommentDto newCommentDto, Long userId, Long commentId);

    CommentDto updateCommentByAdmin(NewCommentDto newCommentDto, Long commentId);

    void deleteCommentByUser(Long userId, Long commentId);

    void deleteCommentByAdmin(Long commentId);

    List<CommentDto> getUserCommentsByCreateTime(Long userId, LocalDateTime createStart, LocalDateTime createEnd, Integer from, Integer size);

    List<CommentDto> getCommentsByCreateTime(LocalDateTime createStart, LocalDateTime createEnd, Integer from, Integer size);

    List<CommentDto> getCommentsByEventId(Long eventId, Integer from, Integer size);

    CommentDto getCommentsById(Long commentId);
}
