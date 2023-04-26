package ru.practicum.comment.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.comment.model.Comment;
import ru.practicum.exception.ConflictException;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class CommentRepositoryImpl implements CommentRepositoryCustom {
    private final EntityManager entityManager;

    @Override
    public List<Comment> getUserCommentsByCreateTime(Long userId, LocalDateTime createStart, LocalDateTime createEnd, Integer from, Integer size) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        Predicate criteria = null;
        CriteriaQuery<Comment> query = builder.createQuery(Comment.class);

        Root<Comment> root = query.from(Comment.class);

        if (createStart != null && createEnd != null) {
            if (createEnd.isBefore(createStart)) {
                throw new ConflictException("End of range must be after start");
            }
        }

        if (userId != null) {
            criteria = root.get("author").in(userId);
        }
        if (createStart != null) {
            Predicate greaterTime = builder.greaterThanOrEqualTo(
                    root.get("created").as(LocalDateTime.class), createStart);
            if (criteria == null) {
                criteria = greaterTime;
            } else {
                criteria = builder.and(criteria, greaterTime);
            }
        }
        if (createEnd != null) {
            Predicate lessTime = builder.lessThanOrEqualTo(
                    root.get("created").as(LocalDateTime.class), createEnd);
            if (criteria == null) {
                criteria = lessTime;
            } else {
                criteria = builder.and(criteria, lessTime);
            }
        }
        if (criteria == null) {
            query.select(root).orderBy(builder.asc(root.get("created")));
        } else {
            query.select(root).where(criteria).orderBy(builder.asc(root.get("created")));
        }
        List<Comment> comments = entityManager.createQuery(query)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
        return comments;
    }
}
