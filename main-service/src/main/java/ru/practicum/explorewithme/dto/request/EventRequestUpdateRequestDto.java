package ru.practicum.explorewithme.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.model.request.enums.EventRequestStatus;
import ru.practicum.explorewithme.model.request.enums.util.EventRequestStatusPattern;

import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventRequestUpdateRequestDto {
    @NotNull
    private List<Long> requestIds;
    @NotNull @EventRequestStatusPattern(regexp = "REJECTED|CONFIRMED")
    private EventRequestStatus status;
}
