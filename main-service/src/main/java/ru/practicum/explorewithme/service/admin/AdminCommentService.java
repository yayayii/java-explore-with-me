package ru.practicum.explorewithme.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dao.CommentDao;
import ru.practicum.explorewithme.model.Comment;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class AdminCommentService {
    private final CommentDao commentDao;

    @Transactional
    public void deleteComment(Long commentId) {
        log.info("main-service - AdminCommentService - deleteComment - commentId: {}", commentId);

        Comment comment = commentDao.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment id = " + commentId + " doesn't exist"));

        commentDao.delete(comment);
    }
}
