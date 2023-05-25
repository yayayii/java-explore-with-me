package ru.practicum.explorewithme.dto.event.enums.util;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.explorewithme.dto.event.enums.SortValue;

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
