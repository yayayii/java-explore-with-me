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
import ru.practicum.explorewithme.service.PublicService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
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


    @BeforeAll
    public static void beforeAll() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testCategoryResponseDto = new CategoryResponseDto(1L, "name1");
    }

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(publicController).build();
    }


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
}