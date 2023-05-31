package ru.practicum.explorewithme.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.dto.comment.CommentRequestDto;
import ru.practicum.explorewithme.dto.comment.CommentResponseDto;
import ru.practicum.explorewithme.model.Comment;
import ru.practicum.explorewithme.model.User;
import ru.practicum.explorewithme.model.event.Event;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {
    public Comment toComment(CommentRequestDto requestDto, Event event, User author, LocalDateTime created) {
        return new Comment(requestDto.getText(), event, author, created);
    }

    public CommentResponseDto toResponseDto(Comment model) {
        return new CommentResponseDto(
                model.getId(), model.getText(), model.getAuthor().getName(), model.getCreated()
        );
    }
}
