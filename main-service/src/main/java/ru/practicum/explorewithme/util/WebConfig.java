package ru.practicum.explorewithme.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.practicum.explorewithme.model.event.enums.util.StringToEventStateConverter;
import ru.practicum.explorewithme.model.event.enums.util.StringToEventUpdateStateConverter;
import ru.practicum.explorewithme.model.event.enums.util.StringToSortValueConverter;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToEventStateConverter());
        registry.addConverter(new StringToEventUpdateStateConverter());
        registry.addConverter(new StringToSortValueConverter());
    }
}
