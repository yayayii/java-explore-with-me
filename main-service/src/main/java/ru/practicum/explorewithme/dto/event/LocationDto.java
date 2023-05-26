package ru.practicum.explorewithme.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LocationDto {
    @Min(-90) @Max(90)
    private Double lat;
    @Min(-180) @Max(180)
    private Double lon;
}
