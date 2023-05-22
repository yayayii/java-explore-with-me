package ru.practicum.explorewithme.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.model.event.Event;
import ru.practicum.explorewithme.model.event.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventDao extends JpaRepository<Event, Long> {
    @Query(
        "select e from Event e " +
        "where (:userIds is null or e.initiator.id in (:userIds)) " +
        "and (:states is null or e.state in (:states)) " +
        "and (:categoryIds is null or e.category.id in (:categoryIds)) " +
        "and (" +
                "(cast(:rangeStart as java.time.LocalDateTime) is null and cast(:rangeEnd as java.time.LocalDateTime) is null) or " +
                "(cast(:rangeStart as java.time.LocalDateTime) is null and e.eventDate < :rangeEnd) or " +
                "(cast(:rangeEnd as java.time.LocalDateTime) is null and :rangeStart < e.eventDate) or " +
                "(e.eventDate between :rangeStart and :rangeEnd) " +
        ")"
    )
    List<Event> searchAllByAdmin(List<Long> userIds, List<EventState> states, List<Long> categoryIds,
                                 LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    @Query(
        "select e from Event e " +
        "where (:text is null or upper(e.annotation) like upper(concat('%', :text, '%')) or upper(e.description) like upper(concat('%', :text, '%'))) " +
        "and ((:categoryIds) is null or e.category.id in (:categoryIds)) " +
        "and (:isPaid is null or e.paid is :isPaid) " +
        "and ( " +
            "(cast(:rangeStart as java.time.LocalDateTime) is null and cast(:rangeEnd as java.time.LocalDateTime) is null) or " +
            "(cast(:rangeStart as java.time.LocalDateTime) is null and e.eventDate < :rangeEnd) or " +
            "(cast(:rangeEnd as java.time.LocalDateTime) is null and :rangeStart < e.eventDate) or " +
            "(e.eventDate between :rangeStart and :rangeEnd) " +
        ") " +
        "and (:onlyAvailable is false or e.confirmedRequests < e.participantLimit) " +
        "and e.state = :eventState"
    )
    List<Event> searchAllByPublic(String text, List<Long> categoryIds, Boolean isPaid, LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd, boolean onlyAvailable, EventState eventState, Pageable pageable);

    List<Event> findAllByInitiator_Id(Long userId, Pageable pageable);

    Optional<Event> findByIdAndState(Long eventId, EventState state);
}
