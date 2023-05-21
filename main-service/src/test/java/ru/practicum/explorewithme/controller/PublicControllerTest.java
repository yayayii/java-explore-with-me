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
import ru.practicum.explorewithme.dto.compilation.CompilationResponseDto;
import ru.practicum.explorewithme.dto.event.EventShortResponseDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
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
    private static ObjectMapper objectMapper;
    private MockMvc mockMvc;

    private static CategoryResponseDto testCategoryResponseDto;
    private static UserResponseDto testUserResponseDto;
    private static LocalDateTime testLocalDateTime;
    private static EventShortResponseDto testEventShortResponseDto;
    private static CompilationResponseDto testCompilationResponseDto;


    @BeforeAll
    public static void beforeAll() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testCategoryResponseDto = new CategoryResponseDto(1L, "name1");
        testUserResponseDto = new UserResponseDto(1L, "email1@email.ru", "name1");
        testLocalDateTime = LocalDateTime.of(2024, 1, 1, 1, 1);
        testEventShortResponseDto = new EventShortResponseDto(
                1L, "title1", "annotation1", false, testCategoryResponseDto, 1,
                testLocalDateTime, 1, testUserResponseDto
        );
        testCompilationResponseDto = new CompilationResponseDto(
                1L, "title1", false, List.of(testEventShortResponseDto, testEventShortResponseDto)
        );
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
        when(mockPublicService.getCompilationById(anyLong()))
                .thenReturn(testCompilationResponseDto);
        mockMvc.perform(get("/compilations/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetCompilations() throws Exception {
        when(mockPublicService.getCompilations(anyBoolean(), anyInt(), anyInt()))
                .thenReturn(List.of(testCompilationResponseDto, testCompilationResponseDto));
        mockMvc.perform(get("/compilations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }
}