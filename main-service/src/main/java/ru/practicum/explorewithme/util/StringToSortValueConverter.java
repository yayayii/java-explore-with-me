package ru.practicum.explorewithme.util;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.explorewithme.model.event.SortValue;

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
