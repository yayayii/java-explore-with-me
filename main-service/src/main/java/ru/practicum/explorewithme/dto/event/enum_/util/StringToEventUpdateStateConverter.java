package ru.practicum.explorewithme.dto.event.enum_.util;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.explorewithme.dto.event.enum_.EventUpdateState;

public class StringToEventUpdateStateConverter implements Converter<String, EventUpdateState> {
    @Override
    public EventUpdateState convert(String source) {
        try {
            return EventUpdateState.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + source);
        }
    }
}
