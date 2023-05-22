package ru.practicum.explorewithme.util;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.explorewithme.model.event.EventUpdateState;

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
