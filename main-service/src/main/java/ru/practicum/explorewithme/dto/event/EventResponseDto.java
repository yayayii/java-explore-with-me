package ru.practicum.explorewithme.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.dto.category.CategoryResponseDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.model.event.EventState;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
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
    @EqualsAndHashCode.Exclude
    private LocalDateTime createdOn;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @EqualsAndHashCode.Exclude
    private LocalDateTime publishedOn;
    private LocationDto location;
    private long views;
    private UserResponseDto initiator;
    private EventState state;
}
