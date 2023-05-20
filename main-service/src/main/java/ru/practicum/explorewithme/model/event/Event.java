package ru.practicum.explorewithme.model.event;

import lombok.*;
import ru.practicum.explorewithme.model.Category;
import ru.practicum.explorewithme.model.User;

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
    private int confirmedRequests;
    private LocalDateTime createdOn;
    private LocalDateTime eventDate;
    private LocalDateTime publishedOn;
    private long views;
    @ManyToOne
    private User initiator;
    private EventState state;
}
