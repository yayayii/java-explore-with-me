package ru.practicum.explorewithme.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventRequestUpdateResponseDto {
    private List<EventRequestResponseDto> confirmedRequests = new ArrayList<>();
    private List<EventRequestResponseDto> rejectedRequests = new ArrayList<>();
}
