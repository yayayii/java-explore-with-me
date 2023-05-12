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
import ru.practicum.explorewithme.controller.StatsController;
import ru.practicum.explorewithme.dto.StatsRequestDto;
import ru.practicum.explorewithme.dto.StatsResponseDto;
import ru.practicum.explorewithme.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class StatsControllerTest {
    @Mock
    private StatsService mockStatsService;
    @InjectMocks
    private StatsController statsController;
    private static ObjectMapper objectMapper;
    private MockMvc mockMvc;


    private static StatsRequestDto testStatsRequestDto;
    private static StatsResponseDto testStatsResponseDto;


    @BeforeAll
    public static void beforeAll() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testStatsRequestDto = new StatsRequestDto("app1", "uri1", "ip1", LocalDateTime.now());
        testStatsResponseDto = new StatsResponseDto("app1", "uri1", 1L);
    }

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(statsController).build();
    }


    @Test
    public void testSaveEndpointRequest() throws Exception {
        doNothing().when(mockStatsService).saveEndpointRequest(any());
        mockMvc.perform(post("/hit")
                .content(objectMapper.writeValueAsString(testStatsRequestDto))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
    }

    @Test
    public void testGetStatsById() throws Exception {
        when(mockStatsService.getStatsById(anyLong()))
                .thenReturn(testStatsResponseDto);
        mockMvc.perform(get("/stats/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetStats() throws Exception {
        when(mockStatsService.getStats(anyString(), anyString(), any(), anyBoolean()))
                .thenReturn(List.of(testStatsResponseDto, testStatsResponseDto));
        mockMvc.perform(get("/stats?start=start&end=end"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
