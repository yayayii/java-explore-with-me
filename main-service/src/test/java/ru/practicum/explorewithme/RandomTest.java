package ru.practicum.explorewithme;


import org.junit.jupiter.api.Test;
import ru.practicum.explorewithme.dto.event.EventShortResponseDto;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RandomTest {
    @Test
    public void test() {
        List<EventShortResponseDto> events = List.of(
                EventShortResponseDto.builder().publishedOn(LocalDateTime.of(2000, 1, 1, 1, 1)).build(),
                EventShortResponseDto.builder().build()
        );

        assertEquals(
                LocalDateTime.of(2000, 1, 1, 1, 1),
                events.stream().filter((event) -> event.getPublishedOn() != null).min(Comparator.comparing(EventShortResponseDto::getPublishedOn)).get().getPublishedOn()
        );
    }
}
