package ru.practicum.explorewithme.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.model.event.Event;
import ru.practicum.explorewithme.model.event.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventDao extends JpaRepository<Event, Long> {
    @Query(
        "select e from Event e " +
        "where e.initiator.id in ?1 " +
        "and e.state in ?2 " +
        "and e.category.id in ?3 " +
        "and e.eventDate between ?4 and ?5"
    )
    List<Event> searchAllByAdmin(long[] users, EventState[] states, long[] categories,
                                 LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    List<Event> findAllByInitiator_Id(Long userId, Pageable pageable);
}
