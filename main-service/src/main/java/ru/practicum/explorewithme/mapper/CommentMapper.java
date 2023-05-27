package ru.practicum.explorewithme.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.dto.comment.CommentRequestDto;
import ru.practicum.explorewithme.dto.comment.CommentResponseDto;
import ru.practicum.explorewithme.model.Comment;

@UtilityClass
public class CommentMapper {
    public Comment toComment(CommentRequestDto requestDto) {
        return new Comment(requestDto.getText());
    }

    public CommentResponseDto toResponseDto(Comment model) {
        return new CommentResponseDto(
                model.getId(), model.getText(), model.getAuthor().getName(), model.getCreated()
        );
    }
}
