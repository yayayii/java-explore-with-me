package ru.practicum.explorewithme.dto.event.enum_.util;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.explorewithme.dto.event.enum_.SortValue;

public class StringToSortValueConverter implements Converter<String, SortValue> {
    @Override
    public SortValue convert(String source) {
        try {
            return SortValue.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + source);
        }
    }
}
