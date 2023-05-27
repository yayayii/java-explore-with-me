package ru.practicum.explorewithme.dto.event.enum_.util;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.explorewithme.model.event.enum_.EventState;

public class StringToEventStateConverter implements Converter<String, EventState> {
    @Override
    public EventState convert(String source) {
        try {
            return EventState.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + source);
        }
    }
}
