package ru.practicum.explorewithme.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LocationDto {
    private double lat;
    private double lon;
}
