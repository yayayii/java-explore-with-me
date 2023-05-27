package ru.practicum.explorewithme.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.explorewithme.dto.category.CategoryResponseDto;
import ru.practicum.explorewithme.dto.comment.CommentResponseDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.model.event.enum_.EventState;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EventResponseDto {
    private Long id;
    private String title;
    private String annotation;
    private String description;
    private boolean paid;
    private boolean requestModeration;
    private CategoryResponseDto category;
    private int participantLimit;
    private int confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @EqualsAndHashCode.Exclude
    private LocalDateTime createdOn;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @EqualsAndHashCode.Exclude
    private LocalDateTime publishedOn;
    private LocationDto location;
    private long views;
    private UserResponseDto initiator;
    private EventState state;
    private List<CommentResponseDto> comments;
}
