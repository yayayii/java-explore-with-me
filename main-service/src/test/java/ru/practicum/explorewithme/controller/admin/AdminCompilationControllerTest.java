package ru.practicum.explorewithme.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.practicum.explorewithme.dto.compilation.CompilationRequestDto;
import ru.practicum.explorewithme.dto.compilation.CompilationResponseDto;
import ru.practicum.explorewithme.dto.event.EventShortResponseDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.model.event.enum_.EventState;
import ru.practicum.explorewithme.service.admin.AdminCompilationService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AdminCompilationControllerTest {
    @Mock
    private AdminCompilationService mockAdminService;
    @Mock
    private StatClient mockStatClient;
    @InjectMocks
    private AdminCompilationController adminController;
    private static ObjectMapper objectMapper;
    private MockMvc mockMvc;

    private static final String API_PREFIX = "/admin/compilations";
    private static CompilationRequestDto testCompilationRequestDto;
    private static CompilationResponseDto testCompilationResponseDto;
    private static StatResponseDto testStatResponseDto;


    @BeforeAll
    public static void beforeAll() {
        objectMapper = new ObjectMapper();

        LocalDateTime testLocalDateTime = LocalDateTime.of(2024, 1, 1, 1, 1);
        EventShortResponseDto testEventShortResponseDto = new EventShortResponseDto(
                1L, "title1", "annotation1", false,
                new CategoryResponseDto(1L, "name1"), 1,
                testLocalDateTime, 1, new UserResponseDto(1L, "email1@email.ru", "name1"),
                EventState.PUBLISHED, testLocalDateTime
        );

        testCompilationRequestDto = new CompilationRequestDto("title1", false, List.of(1L, 2L));
        testCompilationResponseDto = new CompilationResponseDto(
                1L, "title1", false, List.of(testEventShortResponseDto, testEventShortResponseDto)
        );

        testStatResponseDto = new StatResponseDto("app1", "uri1", 1L);
    }

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }


    @Test
    public void testAddCompilation() throws Exception {
        testCompilationRequestDto.setTitle(null);
        mockMvc.perform(post(API_PREFIX)
                        .content(objectMapper.writeValueAsString(testCompilationRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        testCompilationRequestDto.setTitle("title1");

        when(mockStatClient.getStats(any(), any(), any(), anyBoolean()))
                .thenReturn(new ResponseEntity<>(List.of(testStatResponseDto), HttpStatus.OK));
        when(mockAdminService.addCompilation(any()))
                .thenReturn(testCompilationResponseDto);
        mockMvc.perform(post(API_PREFIX)
                        .content(objectMapper.writeValueAsString(testCompilationRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testUpdateCompilation() throws Exception {
        when(mockStatClient.getStats(any(), any(), any(), anyBoolean()))
                .thenReturn(new ResponseEntity<>(List.of(testStatResponseDto), HttpStatus.OK));
        when(mockAdminService.updateCompilation(anyLong(), any()))
                .thenReturn(testCompilationResponseDto);
        mockMvc.perform(patch(API_PREFIX + "/1")
                        .content(objectMapper.writeValueAsString(testCompilationRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testDeleteCompilation() throws Exception {
        doNothing().when(mockAdminService).deleteCompilation(anyLong());
        mockMvc.perform(delete(API_PREFIX + "/1"))
                .andExpect(status().isNoContent());
    }
}
