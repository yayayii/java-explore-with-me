package ru.practicum.explorewithme.controller;

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
import ru.practicum.explorewithme.dto.compilation.CompilationResponseDto;
import ru.practicum.explorewithme.dto.event.EventResponseDto;
import ru.practicum.explorewithme.dto.event.EventShortResponseDto;
import ru.practicum.explorewithme.dto.event.LocationDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.model.event.enums.EventState;
import ru.practicum.explorewithme.service.PublicService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PublicControllerTest {
    @Mock
    private PublicService mockPublicService;
    @Mock
    private StatClient mockStatClient;
    @InjectMocks
    private PublicController publicController;
    private MockMvc mockMvc;

    private static CategoryResponseDto testCategoryResponseDto;
    private static UserResponseDto testUserResponseDto;
    private static LocalDateTime testLocalDateTime;
    private static EventShortResponseDto testEventShortResponseDto;
    private static EventResponseDto testEventResponseDto;
    private static CompilationResponseDto testCompilationResponseDto;
    private static StatResponseDto testStatResponseDto;


    @BeforeAll
    public static void beforeAll() {
        testCategoryResponseDto = new CategoryResponseDto(1L, "name1");
        testUserResponseDto = new UserResponseDto(1L, "email1@email.ru", "name1");
        testLocalDateTime = LocalDateTime.of(2024, 1, 1, 1, 1);
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
        testCompilationResponseDto = new CompilationResponseDto(
                1L, "title1", false, List.of(testEventShortResponseDto, testEventShortResponseDto)
        );
        testStatResponseDto = new StatResponseDto("app1", "uri1", 1L);
    }

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(publicController).build();
    }


    //categories
    @Test
    public void testGetCategoryById() throws Exception {
        when(mockPublicService.getCategoryById(anyLong()))
                .thenReturn(testCategoryResponseDto);
        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetCategories() throws Exception {
        when(mockPublicService.getCategories(anyInt(), anyInt()))
                .thenReturn(List.of(testCategoryResponseDto, testCategoryResponseDto));
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    //compilations
    @Test
    public void testGetCompilationById() throws Exception {
        when(mockStatClient.getStats(any(), any(), any(), anyBoolean()))
                .thenReturn(new ResponseEntity<>(List.of(testStatResponseDto), HttpStatus.OK));
        when(mockPublicService.getCompilationById(anyLong()))
                .thenReturn(testCompilationResponseDto);
        mockMvc.perform(get("/compilations/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetCompilations() throws Exception {
        when(mockStatClient.getStats(any(), any(), any(), anyBoolean()))
                .thenReturn(new ResponseEntity<>(List.of(testStatResponseDto), HttpStatus.OK));
        when(mockPublicService.getCompilations(any(), anyInt(), anyInt()))
                .thenReturn(List.of(testCompilationResponseDto, testCompilationResponseDto));
        mockMvc.perform(get("/compilations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    //events
    @Test
    public void testGetEventById() throws Exception {
        when(mockPublicService.getEventById(anyLong()))
                .thenReturn(testEventResponseDto);
        when(mockStatClient.saveEndpointRequest(any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.CREATED));
        when(mockStatClient.getStats(any(), any(), any(), anyBoolean()))
                .thenReturn(new ResponseEntity<>(List.of(testStatResponseDto), HttpStatus.OK));
        mockMvc.perform(get("/events/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetEvents() throws Exception {
        when(mockPublicService.getEvents(anyString(), anyList(), anyBoolean(), any(), any(), anyBoolean(), anyInt(), anyInt()))
                .thenReturn(List.of(testEventShortResponseDto, testEventShortResponseDto));
        when(mockStatClient.saveEndpointRequest(any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.CREATED));
        when(mockStatClient.getStats(any(), any(), any(), anyBoolean()))
                .thenReturn(new ResponseEntity<>(List.of(testStatResponseDto), HttpStatus.OK));
        mockMvc.perform(get("/events?text=text&categories=1,2&paid=true&rangeStart=2023-01-01 12:12:12&rangeEnd=2023-01-01 12:12:12&onlyAvailable=true&sort=VIEWS&from=1&size=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }
}