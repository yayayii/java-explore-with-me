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
import ru.practicum.explorewithme.dto.request.EventRequestResponseDto;
import ru.practicum.explorewithme.dto.request.EventRequestUpdateRequestDto;
import ru.practicum.explorewithme.dto.request.EventRequestUpdateResponseDto;
import ru.practicum.explorewithme.model.request.enum_.EventRequestStatus;
import ru.practicum.explorewithme.service.private_.PrivateRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PrivateRequestControllerTest {
    @Mock
    private PrivateRequestService mockPrivateService;
    @InjectMocks
    private PrivateRequestController privateController;
    private static ObjectMapper objectMapper;
    private MockMvc mockMvc;

    private static final String API_PREFIX = "/users/1";
    private static EventRequestResponseDto testEventRequestResponseDto;
    private static EventRequestUpdateRequestDto testEventRequestUpdateRequestDto;
    private static EventRequestUpdateResponseDto testEventRequestUpdateResponseDto;


    @BeforeAll
    public static void beforeAll() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        LocalDateTime testLocalDateTime = LocalDateTime.of(2024, 1, 1, 1, 1);
        testEventRequestResponseDto = new EventRequestResponseDto(
                1L, 1L, 1L, testLocalDateTime, EventRequestStatus.CONFIRMED
        );
        testEventRequestUpdateRequestDto = new EventRequestUpdateRequestDto(
                List.of(1L, 2L), EventRequestStatus.CONFIRMED
        );
        testEventRequestUpdateResponseDto = new EventRequestUpdateResponseDto(
                List.of(testEventRequestResponseDto), List.of(testEventRequestResponseDto)
        );
    }

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(privateController).build();
    }

    //requests
    @Test
    public void testAddRequest() throws Exception {
        when(mockPrivateService.addRequest(anyLong(), anyLong()))
                .thenReturn(testEventRequestResponseDto);
        mockMvc.perform(post(API_PREFIX + "/requests?eventId=1"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetRequestsForEvent() throws Exception {
        when(mockPrivateService.getRequestsForEvent(anyLong(), anyLong()))
                .thenReturn(List.of(testEventRequestResponseDto, testEventRequestResponseDto));
        mockMvc.perform(get(API_PREFIX + "/events/1/requests"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testGetRequestsForUser() throws Exception {
        when(mockPrivateService.getRequestsForUser(anyLong()))
                .thenReturn(List.of(testEventRequestResponseDto, testEventRequestResponseDto));
        mockMvc.perform(get(API_PREFIX + "/requests"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testModerateRequests() throws Exception {
        mockMvc.perform(patch(API_PREFIX + "/events/1/requests")
                    .content(objectMapper.writeValueAsString(
                            new EventRequestUpdateRequestDto(null, EventRequestStatus.CONFIRMED))
                    )
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mockMvc.perform(patch(API_PREFIX + "/events/1/requests")
                        .content(objectMapper.writeValueAsString(
                                new EventRequestUpdateRequestDto(List.of(1L, 2L), null))
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mockMvc.perform(patch(API_PREFIX + "/events/1/requests")
                        .content(objectMapper.writeValueAsString(
                                new EventRequestUpdateRequestDto(List.of(1L, 2L), EventRequestStatus.PENDING))
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        when(mockPrivateService.moderateRequests(anyLong(), anyLong(), any()))
                .thenReturn(testEventRequestUpdateResponseDto);
        mockMvc.perform(patch(API_PREFIX + "/events/1/requests")
                    .content(objectMapper.writeValueAsString(testEventRequestUpdateRequestDto))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testCancelRequest() throws Exception {
        when(mockPrivateService.cancelRequest(anyLong(), anyLong()))
                .thenReturn(testEventRequestResponseDto);
        mockMvc.perform(patch(API_PREFIX + "/requests/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
