package ru.practicum.explorewithme.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.model.event.EventUpdateState;
import ru.practicum.explorewithme.util.Admin;
import ru.practicum.explorewithme.util.EventAdminUpdateStatePattern;
import ru.practicum.explorewithme.util.EventPrivateUpdateStatePattern;
import ru.practicum.explorewithme.util.Private;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventUpdateRequestDto {
    @Size(min = 3, max = 120)
    private String title;
    @Size(min = 20, max = 2000)
    private String annotation;
    @Size(min = 20, max = 7000)
    private String description;
    private Boolean paid;
    private Boolean requestModeration;
    private Integer participantLimit;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Future
    private LocalDateTime eventDate;
    private LocationDto location;
    private Long category;
    @NotNull(groups={Admin.class, Private.class})
    @EventAdminUpdateStatePattern(regexp="PUBLISH_EVENT|REJECT_EVENT", groups={Admin.class})
    @EventPrivateUpdateStatePattern(regexp="SEND_TO_REVIEW|CANCEL_REVIEW", groups={Private.class})
    private EventUpdateState stateAction;
}
