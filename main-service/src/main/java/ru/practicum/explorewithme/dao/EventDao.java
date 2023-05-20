package ru.practicum.explorewithme.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.model.event.Event;

@Repository
public interface EventDao extends JpaRepository<Event, Long> {
}
