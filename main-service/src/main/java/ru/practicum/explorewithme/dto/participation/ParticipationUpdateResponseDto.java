package ru.practicum.explorewithme.dto.participation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ParticipationUpdateResponseDto {
    private List<ParticipationResponseDto> confirmedRequests = new ArrayList<>();
    private List<ParticipationResponseDto> rejectedRequests = new ArrayList<>();
}
