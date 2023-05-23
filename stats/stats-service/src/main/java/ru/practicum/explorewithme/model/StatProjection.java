package ru.practicum.explorewithme.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StatProjection {
    private String app;
    private String uri;
    private Long hits;
}
