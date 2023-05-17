package ru.practicum.explorewithme.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class StatProjection {
    private String app;
    private String uri;
    private Long hits;
}