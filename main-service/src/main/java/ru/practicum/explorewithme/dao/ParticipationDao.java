package ru.practicum.explorewithme.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.model.participation.Participation;

@Repository
public interface ParticipationDao extends JpaRepository<Participation, Long> {
    boolean existsByRequester_IdAndEvent_Id(Long requesterId, Long eventId);
}
