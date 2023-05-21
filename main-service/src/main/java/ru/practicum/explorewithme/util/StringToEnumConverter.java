package ru.practicum.explorewithme.util;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.explorewithme.model.event.EventState;

public class StringToEnumConverter implements Converter<String, EventState> {
    @Override
    public EventState convert(String source) {
        try {
            return EventState.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + source);
        }
    }
}
