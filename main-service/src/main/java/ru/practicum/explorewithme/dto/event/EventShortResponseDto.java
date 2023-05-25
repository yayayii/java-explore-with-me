package ru.practicum.explorewithme.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import ru.practicum.explorewithme.dto.category.CategoryResponseDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.model.event.enums.EventState;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EventShortResponseDto {
    private Long id;
    private String title;
    private String annotation;
    private boolean paid;
    private CategoryResponseDto category;
    private int confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private long views;
    private UserResponseDto initiator;
    @JsonIgnore
    private EventState state;
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private LocalDateTime publishedOn;
}
