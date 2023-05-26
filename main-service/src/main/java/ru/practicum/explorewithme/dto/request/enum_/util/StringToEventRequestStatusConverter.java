package ru.practicum.explorewithme.dto.request.enum_.util;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.explorewithme.model.request.enum_.EventRequestStatus;

public class StringToEventRequestStatusConverter implements Converter<String, EventRequestStatus> {
    @Override
    public EventRequestStatus convert(String source) {
        try {
            return EventRequestStatus.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + source);
        }
    }
}
