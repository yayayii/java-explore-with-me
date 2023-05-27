package ru.practicum.explorewithme.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LocationDto {
    @NotNull @Min(-90) @Max(90)
    private Double lat;
    @NotNull @Min(-180) @Max(180)
    private Double lon;
}
