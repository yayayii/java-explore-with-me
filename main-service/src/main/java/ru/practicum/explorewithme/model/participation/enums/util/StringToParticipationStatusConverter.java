package ru.practicum.explorewithme.model.participation.enums.util;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.explorewithme.model.participation.enums.ParticipationStatus;

public class StringToParticipationStatusConverter implements Converter<String, ParticipationStatus> {
    @Override
    public ParticipationStatus convert(String source) {
        try {
            return ParticipationStatus.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + source);
        }
    }
}
