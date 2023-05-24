package ru.practicum.explorewithme;

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
import ru.practicum.explorewithme.controller.StatController;
import ru.practicum.explorewithme.dto.StatRequestDto;
import ru.practicum.explorewithme.dto.StatResponseDto;
import ru.practicum.explorewithme.service.StatService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class StatControllerTest {
    @Mock
    private StatService mockStatService;
    @InjectMocks
    private StatController statController;
    private static ObjectMapper objectMapper;
    private MockMvc mockMvc;

    private static StatRequestDto testStatRequestDto;
    private static StatResponseDto testStatResponseDto;


    @BeforeAll
    public static void beforeAll() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testStatRequestDto = new StatRequestDto("app1", "uri1", "ip1", LocalDateTime.now());
        testStatResponseDto = new StatResponseDto("app1", "uri1", 1L);
    }

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(statController).build();
    }


    @Test
    public void testSaveEndpointRequest() throws Exception {
        doNothing().when(mockStatService).saveEndpointRequest(any());
        mockMvc.perform(post("/hit")
                .content(objectMapper.writeValueAsString(testStatRequestDto))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
    }

    @Test
    public void testGetStats() throws Exception {
        when(mockStatService.getStats(any(), any(), any(), anyBoolean()))
                .thenReturn(List.of(testStatResponseDto, testStatResponseDto));
        mockMvc.perform(get("/stats?start=2022-09-06 10:00:23&end=2022-09-06 10:00:23"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }
}