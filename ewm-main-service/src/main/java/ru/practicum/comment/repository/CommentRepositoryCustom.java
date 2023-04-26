package ru.practicum.comment.repository;

import ru.practicum.comment.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepositoryCustom {
    List<Comment> getUserCommentsByCreateTime(Long userId, LocalDateTime createStart, LocalDateTime createEnd, Integer from, Integer size);
}
