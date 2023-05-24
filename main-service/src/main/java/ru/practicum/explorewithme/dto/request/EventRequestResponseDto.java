package ru.practicum.explorewithme.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.model.request.enums.EventRequestStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventRequestResponseDto {
    private Long id;
    private Long event;
    private Long requester;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @EqualsAndHashCode.Exclude
    private LocalDateTime created;
    private EventRequestStatus status;
}
