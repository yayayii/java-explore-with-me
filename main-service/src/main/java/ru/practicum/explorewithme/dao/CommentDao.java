package ru.practicum.explorewithme.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.model.Comment;

public interface CommentDao extends JpaRepository<Comment, Long>  {
}
