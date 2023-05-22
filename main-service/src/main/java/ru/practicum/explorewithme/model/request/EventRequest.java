package ru.practicum.explorewithme.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.model.User;
import ru.practicum.explorewithme.model.event.Event;
import ru.practicum.explorewithme.model.request.enums.EventRequestStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table
public class EventRequest {
    @Id
    @EqualsAndHashCode.Exclude
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Event event;
    @ManyToOne
    private User requester;
    private LocalDateTime created;
    @Enumerated(value = EnumType.STRING)
    private EventRequestStatus status;

    public EventRequest(Event event, User requester, LocalDateTime created) {
        this.event = event;
        this.requester = requester;
        this.created = created;
    }
}
