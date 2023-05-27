package ru.practicum.explorewithme.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.model.Comment;

import java.util.List;

public interface CommentDao extends JpaRepository<Comment, Long>  {
    List<Comment> findAllByEvent_Id(Long eventId);

    List<Comment> findAllByEvent_IdIn(List<Long> eventIds);
}
