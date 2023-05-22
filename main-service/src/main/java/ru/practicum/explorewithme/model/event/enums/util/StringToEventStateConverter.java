package ru.practicum.explorewithme.model.event.enums.util;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.explorewithme.model.event.enums.EventState;

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
