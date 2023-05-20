package ru.practicum.explorewithme.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventRequestDto {
    @NotNull
    @Size(min = 3, max = 120)
    private String title;
    @NotNull
    @Size(min = 20, max = 2000)
    private String annotation;
    @NotNull
    @Size(min = 20, max = 7000)
    private String description;
    private boolean paid;
    private boolean requestModeration = true;
    private int participantLimit;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @NotNull @Future
    private LocalDateTime eventDate;
    @NotNull
    private LocationDto location;
    @NotNull
    private Long category;
}
