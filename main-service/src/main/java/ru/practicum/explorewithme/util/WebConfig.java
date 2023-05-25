package ru.practicum.explorewithme.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.practicum.explorewithme.dto.event.enums.util.StringToEventStateConverter;
import ru.practicum.explorewithme.dto.event.enums.util.StringToEventUpdateStateConverter;
import ru.practicum.explorewithme.dto.event.enums.util.StringToSortValueConverter;
import ru.practicum.explorewithme.dto.request.enums.util.StringToEventRequestStatusConverter;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToEventStateConverter());
        registry.addConverter(new StringToEventUpdateStateConverter());
        registry.addConverter(new StringToSortValueConverter());
        registry.addConverter(new StringToEventRequestStatusConverter());
    }
}
