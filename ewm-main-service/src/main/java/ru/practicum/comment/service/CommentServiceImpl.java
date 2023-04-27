package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Transactional
    @Override
    public CommentDto createComment(NewCommentDto newCommentDto, Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Unfortunately, you can't create comment, user with id=%s not found", userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Unfortunately, you can't create comment, event with id=%s not found", eventId)));

        Comment comment = new Comment();
        comment.setAuthor(user);
        comment.setEvent(event);
        comment.setCreated(LocalDateTime.now());
        comment.setText(newCommentDto.getText());
        log.info("Comment for event with id {} was created", eventId);
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public CommentDto updateCommentByUser(NewCommentDto newCommentDto, Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Unfortunately, you can't update comment, comment not found"));
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Unfortunately, you can't update comment, user not found");
        }
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ConflictException("Unfortunately, you can't update not your comment");
        }
        comment.setText(newCommentDto.getText());
        Comment newComment = commentRepository.save(comment);
        log.info("Comment with ID = {} was updated", commentId);
        return commentMapper.toCommentDto(newComment);
    }

    @Transactional
    @Override
    public CommentDto updateCommentByAdmin(NewCommentDto newCommentDto, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Unfortunately, you can't update comment, comment not found"));
        comment.setText(newCommentDto.getText());
        Comment newComment = commentRepository.save(comment);
        log.info("Comment with ID = {} was updated", commentId);
        return commentMapper.toCommentDto(newComment);
    }

    @Transactional
    @Override
    public void deleteCommentByUser(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Unfortunately, you can't delete comment, comment not found"));
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Unfortunately, you can't delete comment, user not found");
        }
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ConflictException("Unfortunately, you can't delete not your comment");
        }
        log.info("Comment with ID = {} was deleted", commentId);
        commentRepository.delete(comment);
    }

    @Transactional
    @Override
    public void deleteCommentByAdmin(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Unfortunately, you can't delete comment, comment not found"));
        log.info("Comment with ID = {} was deleted", commentId);
        commentRepository.delete(comment);
    }

    @Override
    public List<CommentDto> getUserCommentsByCreateTime(Long userId, LocalDateTime createStart, LocalDateTime createEnd, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Unfortunately, you can't get the comments, user not found");
        }
        List<Comment> comments = commentRepository.getUserCommentsByCreateTime(userId, createStart, createEnd, from, size);
        log.info("Get comment`s list of user with ID = {}", userId);
        return comments.stream().map(commentMapper::toCommentDto).collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getCommentsByCreateTime(LocalDateTime createStart, LocalDateTime createEnd, Integer from, Integer size) {
        List<Comment> comments = commentRepository.getUserCommentsByCreateTime(null, createStart, createEnd, from, size);
        log.info("Get comment`s list from {} to {}", createStart, createEnd);
        return comments.stream().map(commentMapper::toCommentDto).collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getCommentsByEventId(Long eventId, Integer from, Integer size) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Unfortunately, you can't get comment, event with id=%s not found", eventId)));
        Pageable page = PageRequest.of(from / size, size);
        List<CommentDto> comments = commentRepository.findAllByEvent_Id(eventId, page).stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
        log.info("Get comment`s list of event with ID = {}", eventId);
        return comments;
    }

    @Override
    public CommentDto getCommentsById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Unfortunately, you can't get comment, comment with id=%s not found", commentId)));
        log.info("Comment with ID = {} was found", commentId);
        return commentMapper.toCommentDto(comment);
    }
}
