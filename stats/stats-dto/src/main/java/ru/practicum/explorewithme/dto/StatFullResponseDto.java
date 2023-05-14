package ru.practicum.explorewithme.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StatFullResponseDto {
    private Long id;
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime created;
}
