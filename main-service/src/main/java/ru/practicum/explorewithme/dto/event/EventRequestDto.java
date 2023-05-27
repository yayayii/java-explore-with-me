package ru.practicum.explorewithme.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventRequestDto {
    @NotBlank @Size(min = 3, max = 120)
    private String title;
    @NotBlank @Size(min = 20, max = 2000)
    private String annotation;
    @NotBlank @Size(min = 20, max = 7000)
    private String description;
    private boolean paid;
    private boolean requestModeration = true;
    @PositiveOrZero
    private int participantLimit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull @Future
    private LocalDateTime eventDate;
    @NotNull @Valid
    private LocationDto location;
    @NotNull
    private Long category;
}
