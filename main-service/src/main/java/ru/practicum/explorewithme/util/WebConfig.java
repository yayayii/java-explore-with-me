package ru.practicum.explorewithme.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.practicum.explorewithme.dto.event.enum_.util.StringToEventStateConverter;
import ru.practicum.explorewithme.dto.event.enum_.util.StringToEventUpdateStateConverter;
import ru.practicum.explorewithme.dto.event.enum_.util.StringToSortValueConverter;
import ru.practicum.explorewithme.dto.request.enum_.util.StringToEventRequestStatusConverter;

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
