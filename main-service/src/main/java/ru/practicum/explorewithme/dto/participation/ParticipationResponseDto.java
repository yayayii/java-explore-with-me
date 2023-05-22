package ru.practicum.explorewithme.dto.participation;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.model.participation.enums.ParticipationState;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ParticipationResponseDto {
    private Long id;
    private Long event;
    private Long requester;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @EqualsAndHashCode.Exclude
    private LocalDateTime created;
    private ParticipationState state;
}
