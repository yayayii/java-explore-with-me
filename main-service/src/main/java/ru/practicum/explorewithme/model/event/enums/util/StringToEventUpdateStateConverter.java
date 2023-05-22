package ru.practicum.explorewithme.model.event.enums.util;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.explorewithme.model.event.enums.EventUpdateState;

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
