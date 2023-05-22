package ru.practicum.explorewithme.model.participation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.model.User;
import ru.practicum.explorewithme.model.event.Event;
import ru.practicum.explorewithme.model.participation.enums.ParticipationStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table
public class Participation {
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
    private ParticipationStatus status;

    public Participation(Event event, User requester, LocalDateTime created) {
        this.event = event;
        this.requester = requester;
        this.created = created;
    }
}
