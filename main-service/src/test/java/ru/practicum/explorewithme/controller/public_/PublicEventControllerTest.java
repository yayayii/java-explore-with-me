package ru.practicum.explorewithme.controller.public_;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.explorewithme.dto.category.CategoryResponseDto;
import ru.practicum.explorewithme.dto.event.EventResponseDto;
import ru.practicum.explorewithme.dto.event.EventShortResponseDto;
import ru.practicum.explorewithme.dto.event.LocationDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.model.event.enum_.EventState;
import ru.practicum.explorewithme.service.StatGateway;
import ru.practicum.explorewithme.service.public_.PublicEventService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PublicEventControllerTest {
    @Mock
    private PublicEventService mockPublicService;
    @Mock
    private StatGateway mockStatGateway;
    @InjectMocks
    private PublicEventController publicController;
    private MockMvc mockMvc;

    private static final String API_PREFIX = "/events";
    private static EventShortResponseDto testEventShortResponseDto;
    private static EventResponseDto testEventResponseDto;


    @BeforeAll
    public static void beforeAll() {
        CategoryResponseDto testCategoryResponseDto = new CategoryResponseDto(1L, "name1");

        UserResponseDto testUserResponseDto = new UserResponseDto(1L, "email1@email.ru", "name1");

        LocalDateTime testLocalDateTime = LocalDateTime.of(2024, 1, 1, 1, 1);
        testEventShortResponseDto = new EventShortResponseDto(
                1L, "title1", "annotation1", false, testCategoryResponseDto, 1,
                testLocalDateTime, 1, testUserResponseDto, EventState.PUBLISHED, testLocalDateTime
        );
        testEventResponseDto = new EventResponseDto(
                1L, "title1", "annotation1", "description1", false,
                false, testCategoryResponseDto, 1, 1,
                testLocalDateTime, testLocalDateTime, testLocalDateTime, new LocationDto(1.1, 1.1), 1,
                testUserResponseDto, EventState.PUBLISHED
        );
    }

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(publicController).build();
    }


    //events
    @Test
    public void testGetEventById() throws Exception {
        when(mockPublicService.getEventById(anyLong()))
                .thenReturn(testEventResponseDto);
        when(mockStatGateway.getViewsForEvent(any(), anyLong()))
                .thenReturn(0L);
        mockMvc.perform(get(API_PREFIX + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetEvents() throws Exception {
        when(mockPublicService.getEvents(anyString(), anyList(), anyBoolean(), any(), any(), anyBoolean(), anyInt(), anyInt()))
                .thenReturn(List.of(testEventShortResponseDto, testEventShortResponseDto));
        when(mockStatGateway.getShortEventsWithViews(any()))
                .thenReturn(List.of(testEventShortResponseDto, testEventShortResponseDto));
        mockMvc.perform(get(API_PREFIX + "?text=text&categories=1,2&paid=true&rangeStart=2023-01-01 12:12:12&rangeEnd=2023-01-01 12:12:12&onlyAvailable=true&sort=VIEWS&from=1&size=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
