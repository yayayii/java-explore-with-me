package ru.practicum.explorewithme.controller;

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
import ru.practicum.explorewithme.StatClient;
import ru.practicum.explorewithme.dto.category.CategoryResponseDto;
import ru.practicum.explorewithme.dto.event.EventRequestDto;
import ru.practicum.explorewithme.dto.event.EventResponseDto;
import ru.practicum.explorewithme.dto.event.LocationDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.model.event.EventState;
import ru.practicum.explorewithme.service.PrivateService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PrivateControllerTest {
    @Mock
    private PrivateService mockPrivateService;
    @Mock
    private StatClient mockStatClient;
    @InjectMocks
    private PrivateController privateController;
    private static ObjectMapper objectMapper;
    private MockMvc mockMvc;

    private static LocalDateTime testLocalDateTime;
    private static EventRequestDto testEventRequestDto;
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
        testEventResponseDto = new EventResponseDto(
                1L, "title1", "annotation1", "description1", false,
                false, new CategoryResponseDto(1L, "name1"), 1, 1,
                testLocalDateTime, testLocalDateTime, testLocalDateTime, new LocationDto(1.1, 1.1), 1,
                new UserResponseDto(1L, "email1@yandex.ru", "name1"), EventState.PUBLISHED
        );
    }

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(privateController).build();
    }


    //events
    @Test
    public void testAddEvent() throws Exception {
        testEventRequestDto.setTitle(null);
        mockMvc.perform(post("/users/1/events")
                        .content(objectMapper.writeValueAsString(testEventRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        testEventRequestDto.setTitle("1");
        mockMvc.perform(post("/users/1/events")
                        .content(objectMapper.writeValueAsString(testEventRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        testEventRequestDto.setTitle("title1");

        testEventRequestDto.setAnnotation(null);
        mockMvc.perform(post("/users/1/events")
                        .content(objectMapper.writeValueAsString(testEventRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        testEventRequestDto.setAnnotation("1");
        mockMvc.perform(post("/users/1/events")
                        .content(objectMapper.writeValueAsString(testEventRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        testEventRequestDto.setAnnotation("annotation11111111111");

        testEventRequestDto.setDescription(null);
        mockMvc.perform(post("/users/1/events")
                        .content(objectMapper.writeValueAsString(testEventRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        testEventRequestDto.setDescription("1");
        mockMvc.perform(post("/users/1/events")
                        .content(objectMapper.writeValueAsString(testEventRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        testEventRequestDto.setDescription("description1111111111");

        testEventRequestDto.setEventDate(null);
        mockMvc.perform(post("/users/1/events")
                        .content(objectMapper.writeValueAsString(testEventRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        testEventRequestDto.setEventDate(LocalDateTime.now());
        mockMvc.perform(post("/users/1/events")
                        .content(objectMapper.writeValueAsString(testEventRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        testEventRequestDto.setEventDate(testLocalDateTime);

        testEventRequestDto.setLocation(null);
        mockMvc.perform(post("/users/1/events")
                        .content(objectMapper.writeValueAsString(testEventRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        testEventRequestDto.setLocation(new LocationDto(1.1, 1.1));

        testEventRequestDto.setCategory(null);
        mockMvc.perform(post("/users/1/events")
                        .content(objectMapper.writeValueAsString(testEventRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        testEventRequestDto.setCategory(1L);

        when(mockPrivateService.addEvent(anyLong(), any()))
                .thenReturn(testEventResponseDto);
        mockMvc.perform(post("/users/1/events")
                        .content(objectMapper.writeValueAsString(testEventRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
