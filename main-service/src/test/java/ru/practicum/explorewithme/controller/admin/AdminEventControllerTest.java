package ru.practicum.explorewithme.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.explorewithme.StatClient;
import ru.practicum.explorewithme.dto.StatResponseDto;
import ru.practicum.explorewithme.dto.category.CategoryResponseDto;
import ru.practicum.explorewithme.dto.event.EventResponseDto;
import ru.practicum.explorewithme.dto.event.EventUpdateRequestDto;
import ru.practicum.explorewithme.dto.event.LocationDto;
import ru.practicum.explorewithme.dto.event.enum_.EventUpdateState;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.model.event.enum_.EventState;
import ru.practicum.explorewithme.service.admin.AdminEventService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AdminEventControllerTest {
    @Mock
    private AdminEventService mockAdminService;
    @Mock
    private StatClient mockStatClient;
    @InjectMocks
    private AdminEventController adminController;
    private static ObjectMapper objectMapper;
    private MockMvc mockMvc;

    private static final String API_PREFIX = "/admin/events";
    private static EventUpdateRequestDto testEventUpdateRequestDto;
    private static EventResponseDto testEventResponseDto;
    private static StatResponseDto testStatResponseDto;


    @BeforeAll
    public static void beforeAll() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        LocalDateTime testLocalDateTime = LocalDateTime.of(2024, 1, 1, 1, 1);
        testEventUpdateRequestDto = new EventUpdateRequestDto(
                "newTitle1", "newAnnotation11111111", "newDescription1111111",
                false, false, 1, testLocalDateTime,
                new LocationDto(0.0, 0.0), 1L, EventUpdateState.PUBLISH_EVENT
        );
        testEventResponseDto = new EventResponseDto(
                1L, "title1", "annotation1", "description1", false,
                false, new CategoryResponseDto(1L, "name1"), 1, 1,
                testLocalDateTime, testLocalDateTime, testLocalDateTime, new LocationDto(1.1, 1.1), 1,
                new UserResponseDto(1L, "email1@email.ru", "name1"), EventState.PUBLISHED
        );

        testStatResponseDto = new StatResponseDto("app1", "uri1", 1L);
    }

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }


    @Test
    public void testSearchEvents() throws Exception {
        when(mockAdminService.searchEvents(any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(testEventResponseDto, testEventResponseDto));
        when(mockStatClient.getStats(any(), any(), any(), anyBoolean()))
                .thenReturn(new ResponseEntity<>(List.of(testStatResponseDto), HttpStatus.OK));
        mockMvc.perform(get(API_PREFIX + "?users=1,2&states=PUBLISHED,PENDING&categories=1,2&rangeStart=2023-01-01 12:12:12&rangeEnd=2023-01-01 12:12:12&from=1&size=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
        mockMvc.perform(get(API_PREFIX))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testUpdateEvent() throws Exception {
        testEventUpdateRequestDto.setStateAction(EventUpdateState.CANCEL_REVIEW);
        mockMvc.perform(patch(API_PREFIX + "/1")
                        .content(objectMapper.writeValueAsString(testEventUpdateRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        testEventUpdateRequestDto.setStateAction(EventUpdateState.SEND_TO_REVIEW);
        mockMvc.perform(patch(API_PREFIX + "/1")
                        .content(objectMapper.writeValueAsString(testEventUpdateRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        testEventUpdateRequestDto.setStateAction(EventUpdateState.PUBLISH_EVENT);

        when(mockAdminService.updateEvent(anyLong(), any()))
                .thenReturn(testEventResponseDto);
        mockMvc.perform(patch(API_PREFIX + "/1")
                        .content(objectMapper.writeValueAsString(testEventUpdateRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
