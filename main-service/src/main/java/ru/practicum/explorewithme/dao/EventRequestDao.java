package ru.practicum.explorewithme.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.model.request.EventRequest;

import java.util.List;

@Repository
public interface EventRequestDao extends JpaRepository<EventRequest, Long> {
    boolean existsByRequester_IdAndEvent_Id(Long requesterId, Long eventId);

    List<EventRequest> findAllByRequester_Id(Long requesterId);

    List<EventRequest> findAllByEvent_Id(Long eventId);
}
