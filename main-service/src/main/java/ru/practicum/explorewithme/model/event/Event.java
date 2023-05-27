package ru.practicum.explorewithme.model.event;

import lombok.*;
import org.hibernate.annotations.Formula;
import ru.practicum.explorewithme.model.Category;
import ru.practicum.explorewithme.model.User;
import ru.practicum.explorewithme.model.event.enum_.EventState;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table
public class Event {
    @Id
    @EqualsAndHashCode.Exclude
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String annotation;
    private String description;
    private boolean paid;
    private boolean requestModeration;
    @ManyToOne
    private Category category;
    private double locationLat;
    private double locationLon;
    private int participantLimit;
    @Formula(" (select count(*) from event_request er where er.event_id = id and er.status like 'CONFIRMED') ")
    private int confirmedRequests;
    private LocalDateTime createdOn;
    private LocalDateTime eventDate;
    private LocalDateTime publishedOn;
    @ManyToOne
    private User initiator;
    @Enumerated(value = EnumType.STRING)
    private EventState state;
}
