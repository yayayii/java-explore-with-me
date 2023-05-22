package ru.practicum.explorewithme.dto.participation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.model.participation.enums.ParticipationState;
import ru.practicum.explorewithme.model.participation.enums.util.ParticipationStatePattern;

import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ParticipationUpdateRequestDto {
    @NotNull
    private List<Long> requestIds;
    @NotNull @ParticipationStatePattern(regexp="REJECTED|CONFIRMED")
    private ParticipationState state;
}
