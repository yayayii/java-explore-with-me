package ru.practicum.explorewithme.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.model.request.enum_.EventRequestStatus;
import ru.practicum.explorewithme.dto.request.enum_.util.EventRequestStatusPattern;

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
