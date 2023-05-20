package ru.practicum.explorewithme.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventRequestDto {
    private String title;
    private String annotation;
    private String description;
    private boolean paid;
    private boolean requestModeration;
    private int participantLimit;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private LocationDto location;
    private Long category;
}
