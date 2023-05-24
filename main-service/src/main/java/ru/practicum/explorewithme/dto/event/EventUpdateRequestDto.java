package ru.practicum.explorewithme.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.model.event.enums.EventUpdateState;
import ru.practicum.explorewithme.util.Admin;
import ru.practicum.explorewithme.model.event.enums.util.EventAdminUpdateStatePattern;
import ru.practicum.explorewithme.model.event.enums.util.EventPrivateUpdateStatePattern;
import ru.practicum.explorewithme.util.Private;

import javax.validation.constraints.Future;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventUpdateRequestDto {
    @Size(min = 3, max = 120, groups = {Admin.class, Private.class})
    private String title;
    @Size(min = 20, max = 2000, groups = {Admin.class, Private.class})
    private String annotation;
    @Size(min = 20, max = 7000, groups = {Admin.class, Private.class})
    private String description;
    private Boolean paid;
    private Boolean requestModeration;
    private Integer participantLimit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Future(groups = {Admin.class, Private.class})
    private LocalDateTime eventDate;
    private LocationDto location;
    private Long category;
    @EventAdminUpdateStatePattern(regexp = "PUBLISH_EVENT|REJECT_EVENT", groups = {Admin.class})
    @EventPrivateUpdateStatePattern(regexp = "SEND_TO_REVIEW|CANCEL_REVIEW", groups = {Private.class})
    private EventUpdateState stateAction;
}
