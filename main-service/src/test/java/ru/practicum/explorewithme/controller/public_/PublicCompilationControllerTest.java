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
import ru.practicum.explorewithme.dto.compilation.CompilationResponseDto;
import ru.practicum.explorewithme.dto.event.EventShortResponseDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.model.event.enum_.EventState;
import ru.practicum.explorewithme.service.StatGateway;
import ru.practicum.explorewithme.service.public_.PublicCompilationService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PublicCompilationControllerTest {
    @Mock
    private PublicCompilationService mockPublicService;
    @Mock
    private StatGateway mockStatService;
    @InjectMocks
    private PublicCompilationController publicController;
    private MockMvc mockMvc;

    private static final String API_PREFIX = "/compilations";
    private static CompilationResponseDto testCompilationResponseDto;
    private static EventShortResponseDto[] testEventShortResponseDtos;


    @BeforeAll
    public static void beforeAll() {
        LocalDateTime testLocalDateTime = LocalDateTime.of(2024, 1, 1, 1, 1);
        UserResponseDto[] testUserResponseDtos = new UserResponseDto[]{
                new UserResponseDto(1L, "email1@email.ru", "name1"),
                new UserResponseDto(2L, "email2@email.ru", "name2")
        };
        CategoryResponseDto[] testCategoryResponseDtos = new CategoryResponseDto[]{
                new CategoryResponseDto(1L, "name1"),
                new CategoryResponseDto(2L, "name2")
        };
        testEventShortResponseDtos = new EventShortResponseDto[]{
                new EventShortResponseDto(
                        1L, "title1", "annotation1", false,
                        testCategoryResponseDtos[0], 1,
                        testLocalDateTime, 1, testUserResponseDtos[0],
                        EventState.PUBLISHED, testLocalDateTime
                ),
                new EventShortResponseDto(
                        2L, "title2", "annotation2", false,
                        testCategoryResponseDtos[1], 1,
                        testLocalDateTime, 1, testUserResponseDtos[1],
                        EventState.PUBLISHED, testLocalDateTime
                ),
        };

        testCompilationResponseDto = new CompilationResponseDto(
                1L, "title1", false, List.of(testEventShortResponseDtos[0], testEventShortResponseDtos[1])
        );
    }

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(publicController).build();
    }


    @Test
    public void testGetCompilationById() throws Exception {
        when(mockPublicService.getCompilationById(anyLong()))
                .thenReturn(testCompilationResponseDto);
        when(mockStatService.getShortEventsWithViews(any()))
                .thenReturn(List.of(testEventShortResponseDtos[0], testEventShortResponseDtos[1]));
        mockMvc.perform(get(API_PREFIX + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetCompilations() throws Exception {
        when(mockPublicService.getCompilations(any(), anyInt(), anyInt()))
                .thenReturn(List.of(testCompilationResponseDto, testCompilationResponseDto));
        when(mockStatService.getShortEventsWithViews(any()))
                .thenReturn(List.of(testEventShortResponseDtos[0], testEventShortResponseDtos[1]));
        mockMvc.perform(get(API_PREFIX))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
