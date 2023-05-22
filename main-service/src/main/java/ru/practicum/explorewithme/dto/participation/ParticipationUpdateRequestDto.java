package ru.practicum.explorewithme.dto.participation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.model.participation.enums.ParticipationStatus;
import ru.practicum.explorewithme.model.participation.enums.util.ParticipationStatusPattern;

import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ParticipationUpdateRequestDto {
    @NotNull
    private List<Long> requestIds;
    @NotNull @ParticipationStatusPattern(regexp="REJECTED|CONFIRMED")
    private ParticipationStatus state;
}
