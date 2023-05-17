package ru.practicum.explorewithme.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table
public class StatModel {
    @Id
    @EqualsAndHashCode.Exclude
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime created;

    public StatModel(String app, String uri, String ip, LocalDateTime created) {
        this.app = app;
        this.uri = uri;
        this.ip = ip;
        this.created = created;
    }
}
