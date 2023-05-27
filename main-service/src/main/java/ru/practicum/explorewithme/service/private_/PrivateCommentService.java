package ru.practicum.explorewithme.service.private_;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dao.CommentDao;
import ru.practicum.explorewithme.dao.EventDao;
import ru.practicum.explorewithme.dao.UserDao;
import ru.practicum.explorewithme.dto.comment.CommentRequestDto;
import ru.practicum.explorewithme.dto.comment.CommentResponseDto;
import ru.practicum.explorewithme.mapper.CommentMapper;
import ru.practicum.explorewithme.model.Comment;
import ru.practicum.explorewithme.model.User;
import ru.practicum.explorewithme.model.event.Event;
import ru.practicum.explorewithme.model.event.enum_.EventState;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class PrivateCommentService {
    private final CommentDao commentDao;
    private final EventDao eventDao;
    private final UserDao userDao;


    @Transactional
    public CommentResponseDto addComment(Long userId, Long eventId, CommentRequestDto requestDto) {
        log.info("main-service - PrivateCommentService - addComment - userId: {} / eventId: {} / requestDto: {}",
                userId, eventId, requestDto);

        User author = userDao.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User id = " + userId + " doesn't exist"));
        Event event = eventDao.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Event id = " + eventId + " doesn't exist"));
        if (event.getState() != EventState.PUBLISHED) {
            throw new DataIntegrityViolationException("The event isn't published");
        }

        Comment comment = CommentMapper.toComment(requestDto);
        comment.setAuthor(author);
        comment.setEvent(event);
        comment.setCreated(LocalDateTime.now());

        return CommentMapper.toResponseDto(commentDao.save(comment));
    }

    @Transactional
    public CommentResponseDto editComment(Long userId, Long commentId, Long eventId, CommentRequestDto requestDto) {
        log.info("main-service - PrivateCommentService - editComment - userId: {} / eventId: {} / commentId: {} / requestDto: {}",
                userId, eventId, commentId, requestDto);

        if (!userDao.existsById(userId)) {
            throw new NoSuchElementException("User id = " + userId + " doesn't exist");
        }
        if (!eventDao.existsById(eventId)) {
            throw new NoSuchElementException("Event id = " + eventId + " doesn't exist");
        }
        Comment comment = commentDao.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment id = " + commentId + " doesn't exist"));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new DataIntegrityViolationException("You must be the author of the comment");
        }

        comment.setText(requestDto.getText());

        return CommentMapper.toResponseDto(comment);
    }

    @Transactional
    public void deleteComment(Long userId, Long eventId, Long commentId) {
        log.info("main-service - PrivateCommentService - deleteComment - userId: {} / commentId: {} / eventId: {}",
                userId, commentId, eventId);

        if (!userDao.existsById(userId)) {
            throw new NoSuchElementException("User id = " + userId + " doesn't exist");
        }
        if (!eventDao.existsById(eventId)) {
            throw new NoSuchElementException("Event id = " + eventId + " doesn't exist");
        }
        Comment comment = commentDao.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment id = " + commentId + " doesn't exist"));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new DataIntegrityViolationException("You must be the author of the comment");
        }

        commentDao.delete(comment);
    }
}
