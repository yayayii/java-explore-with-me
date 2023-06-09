package ru.practicum.explorewithme.controller.private_;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import ru.practicum.explorewithme.dto.event.*;
import ru.practicum.explorewithme.dto.event.enum_.EventUpdateState;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.model.event.enum_.EventState;
import ru.practicum.explorewithme.service.StatGateway;
import ru.practicum.explorewithme.service.private_.PrivateEventService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PrivateEventControllerTest {
    @Mock
    private PrivateEventService mockPrivateService;
    @Mock
    private StatGateway mockStatService;
    @InjectMocks
    private PrivateEventController privateController;
    private static ObjectMapper objectMapper;
    private MockMvc mockMvc;

    private static final String API_PREFIX = "/users/1/events";
    private static LocalDateTime testLocalDateTime;
    private static EventRequestDto testEventRequestDto;
    private static EventUpdateRequestDto testEventUpdateRequestDto;
    private static EventShortResponseDto testEventShortResponseDto;
    private static EventResponseDto testEventResponseDto;


    @BeforeAll
    public static void beforeAll() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testLocalDateTime = LocalDateTime.of(2024, 1, 1, 1, 1);
        testEventRequestDto = new EventRequestDto(
                "title1", "annotation11111111111", "description1111111111", false,
                false, 1, testLocalDateTime, new LocationDto(1.1, 1.1), 1L
        );
        testEventUpdateRequestDto = new EventUpdateRequestDto(
                "newTitle1", "newAnnotation11111111", "newDescription1111111",
                false, false, 1, testLocalDateTime,
                new LocationDto(0.0, 0.0), 1L, EventUpdateState.REJECT_EVENT
        );
        testEventShortResponseDto = new EventShortResponseDto(
                1L, "title1", "annotation1", false,
                new CategoryResponseDto(1L, "name1"), 1,testLocalDateTime,
                1, new UserResponseDto(1L, "name1", "email1@yandex.ru"),
                EventState.PUBLISHED, testLocalDateTime
        );
        testEventResponseDto = new EventResponseDto(
                1L, "title1", "annotation1", "description1", false,
                false, new CategoryResponseDto(1L, "name1"), 1, 1,
                testLocalDateTime, testLocalDateTime, testLocalDateTime, new LocationDto(1.1, 1.1), 1,
                new UserResponseDto(1L, "name1", "email1@yandex.ru"), EventState.PUBLISHED, Collections.emptyList()
        );
    }

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(privateController).build();
    }


    @Test
    public void testAddEvent() throws Exception {
        testEventRequestDto.setTitle(null);
        mockMvc.perform(post(API_PREFIX)
                        .content(objectMapper.writeValueAsString(testEventRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        testEventRequestDto.setTitle("1");
        mockMvc.perform(post(API_PREFIX)
                        .content(objectMapper.writeValueAsString(testEventRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        testEventRequestDto.setTitle("title1");

        testEventRequestDto.setAnnotation(null);
        mockMvc.perform(post(API_PREFIX)
                        .content(objectMapper.writeValueAsString(testEventRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        testEventRequestDto.setAnnotation("1");
        mockMvc.perform(post(API_PREFIX)
                        .content(objectMapper.writeValueAsString(testEventRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        testEventRequestDto.setAnnotation("annotation11111111111");

        testEventRequestDto.setDescription(null);
        mockMvc.perform(post(API_PREFIX)
                        .content(objectMapper.writeValueAsString(testEventRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        testEventRequestDto.setDescription("1");
        mockMvc.perform(post(API_PREFIX)
                        .content(objectMapper.writeValueAsString(testEventRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        testEventRequestDto.setDescription("description1111111111");

        testEventRequestDto.setEventDate(null);
        mockMvc.perform(post(API_PREFIX)
                        .content(objectMapper.writeValueAsString(testEventRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        testEventRequestDto.setEventDate(LocalDateTime.now());
        mockMvc.perform(post(API_PREFIX)
                        .content(objectMapper.writeValueAsString(testEventRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        testEventRequestDto.setEventDate(testLocalDateTime);

        testEventRequestDto.setLocation(null);
        mockMvc.perform(post(API_PREFIX)
                        .content(objectMapper.writeValueAsString(testEventRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        testEventRequestDto.setLocation(new LocationDto(1.1, 1.1));

        testEventRequestDto.setCategory(null);
        mockMvc.perform(post(API_PREFIX)
                        .content(objectMapper.writeValueAsString(testEventRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        testEventRequestDto.setCategory(1L);

        when(mockPrivateService.addEvent(anyLong(), any()))
                .thenReturn(testEventResponseDto);
        mockMvc.perform(post(API_PREFIX)
                        .content(objectMapper.writeValueAsString(testEventRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetEventsByInitiatorId() throws Exception {
        when(mockPrivateService.getEventsByInitiatorId(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(testEventShortResponseDto, testEventShortResponseDto));
        when(mockStatService.getShortEventsWithViews(any()))
                .thenReturn(List.of(testEventShortResponseDto, testEventShortResponseDto));
        mockMvc.perform(get(API_PREFIX + "?from=1&size=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testGetEventById() throws Exception {
        when(mockPrivateService.getEventById(anyLong(), anyLong()))
                .thenReturn(testEventResponseDto);
        when(mockStatService.getViewsForEvent(any(), anyLong()))
                .thenReturn(0L);
        mockMvc.perform(get(API_PREFIX + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testUpdateEvent() throws Exception {
        mockMvc.perform(patch(API_PREFIX + "/1")
                        .content(objectMapper.writeValueAsString(testEventUpdateRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        testEventUpdateRequestDto.setStateAction(EventUpdateState.REJECT_EVENT);
        mockMvc.perform(patch(API_PREFIX + "/1")
                        .content(objectMapper.writeValueAsString(testEventUpdateRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        testEventUpdateRequestDto.setStateAction(EventUpdateState.CANCEL_REVIEW);

        when(mockPrivateService.updateEvent(anyLong(), anyLong(), any()))
                .thenReturn(testEventResponseDto);
        mockMvc.perform(patch(API_PREFIX + "/1")
                        .content(objectMapper.writeValueAsString(testEventUpdateRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
