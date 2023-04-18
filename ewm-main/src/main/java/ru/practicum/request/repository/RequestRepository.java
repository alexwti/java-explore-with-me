package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.request.model.Request;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByRequesterId(Long userId);

    Optional<Request> findByIdAndRequesterId(Long requestId, Long userId);

    @Query("select Request from Request Request " +
            "where Request.event.id = :eventId " +
            "and Request.event.initiator.id = :userId")
    List<Request> findByEventIdAndInitiatorId(@Param("eventId") Long eventId,
                                              @Param("userId") Long userId);

    @Query("select p from Request p " +
            "where p.event.id = :eventId and p.status = 'CONFIRMED'")
    List<Request> findByEventIdConfirmed(@Param("eventId") Long eventId);


    @Query("select Request from Request Request " +
            "where Request.event.id = :event " +
            "and Request.id IN (:requestIds)")
    List<Request> findByEventIdAndRequestsIds(@Param("event") Long eventId,
                                              @Param("requestIds") List<Long> requestIds);

    Boolean existsByRequesterIdAndEventId(Long userId, Long eventId);
}
