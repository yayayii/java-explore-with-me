package ru.practicum.explorewithme.model.participation.enums.util;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.explorewithme.model.participation.enums.ParticipationState;

public class StringToParticipationStateConverter implements Converter<String, ParticipationState> {
    @Override
    public ParticipationState convert(String source) {
        try {
            return ParticipationState.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + source);
        }
    }
}
