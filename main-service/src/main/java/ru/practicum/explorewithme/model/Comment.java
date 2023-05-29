package ru.practicum.explorewithme.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.model.event.Event;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table
public class Comment {
    @Id
    @EqualsAndHashCode.Exclude
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    @ManyToOne
    private Event event;
    @ManyToOne
    private User author;
    private LocalDateTime created;

    public Comment(String text, Event event, User author, LocalDateTime created) {
        this.text = text;
        this.event = event;
        this.author = author;
        this.created = created;
    }
}
