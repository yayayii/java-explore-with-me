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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.explorewithme.StatClient;
import ru.practicum.explorewithme.dto.StatResponseDto;
import ru.practicum.explorewithme.dto.category.CategoryResponseDto;
import ru.practicum.explorewithme.dto.event.*;
import ru.practicum.explorewithme.dto.request.EventRequestResponseDto;
import ru.practicum.explorewithme.dto.request.EventRequestUpdateRequestDto;
import ru.practicum.explorewithme.dto.request.EventRequestUpdateResponseDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.model.event.enums.EventState;
import ru.practicum.explorewithme.model.event.enums.EventUpdateState;
import ru.practicum.explorewithme.model.request.enums.EventRequestStatus;
import ru.practicum.explorewithme.service.PrivateService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    private static EventUpdateRequestDto testEventUpdateRequestDto;
    private static EventShortResponseDto testEventShortResponseDto;
    private static EventResponseDto testEventResponseDto;
    private static EventRequestResponseDto testEventRequestResponseDto;
    private static EventRequestUpdateRequestDto testEventRequestUpdateRequestDto;
    private static EventRequestUpdateResponseDto testEventRequestUpdateResponseDto;
    private static StatResponseDto testStatResponseDto;


    @BeforeAll
    public static void beforeAll() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testLocalDateTime = LocalDateTime.of(2024, 1, 1, 1, 1);
        testEventRequestDto = new EventRequestDto(
                "title1", "annotation11111111111", "description1111111111", false,
                false, 1, testLocalDateTime, new LocationDto(1.1, 1.1), 1L
        );
        testEventUpdateRequestDto = new EventUpdateRequestDto(
                "newTitle1", "newAnnotation11111111", "newDescription1111111",
                false, false, 1, testLocalDateTime,
                new LocationDto(0.0, 0.0), 1L, EventUpdateState.REJECT_EVENT
        );
        testEventShortResponseDto = new EventShortResponseDto(
                1L, "title1", "annotation1", false,
                new CategoryResponseDto(1L, "name1"), 1, testLocalDateTime, 1,
                new UserResponseDto(1L, "email1@yandex.ru", "name1"), EventState.PUBLISHED
        );
        testEventResponseDto = new EventResponseDto(
                1L, "title1", "annotation1", "description1", false,
                false, new CategoryResponseDto(1L, "name1"), 1, 1,
                testLocalDateTime, testLocalDateTime, testLocalDateTime, new LocationDto(1.1, 1.1), 1,
                new UserResponseDto(1L, "email1@yandex.ru", "name1"), EventState.PUBLISHED
        );
        testEventRequestResponseDto = new EventRequestResponseDto(
                1L, 1L, 1L, testLocalDateTime, EventRequestStatus.CONFIRMED
        );
        testEventRequestUpdateRequestDto = new EventRequestUpdateRequestDto(
                List.of(1L, 2L), EventRequestStatus.CONFIRMED
        );
        testEventRequestUpdateResponseDto = new EventRequestUpdateResponseDto(
                List.of(testEventRequestResponseDto), List.of(testEventRequestResponseDto)
        );

        testStatResponseDto = new StatResponseDto("app1", "uri1", 1L);
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

    @Test
    public void testGetEventById() throws Exception {
        when(mockPrivateService.getEventById(anyLong(), anyLong()))
                .thenReturn(testEventResponseDto);
        when(mockStatClient.getStats(any(), any(), any(), anyBoolean()))
                .thenReturn(new ResponseEntity<>(List.of(testStatResponseDto), HttpStatus.OK));
        mockMvc.perform(get("/users/1/events/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetEventsByInitiatorId() throws Exception {
        when(mockPrivateService.getEventsByInitiatorId(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(testEventShortResponseDto, testEventShortResponseDto));
        when(mockStatClient.getStats(any(), any(), any(), anyBoolean()))
                .thenReturn(new ResponseEntity<>(List.of(testStatResponseDto), HttpStatus.OK));
        mockMvc.perform(get("/users/1/events?from=1&size=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testUpdateEvent() throws Exception {
        mockMvc.perform(patch("/users/1/events/1")
                        .content(objectMapper.writeValueAsString(testEventUpdateRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        testEventUpdateRequestDto.setStateAction(EventUpdateState.REJECT_EVENT);
        mockMvc.perform(patch("/users/1/events/1")
                        .content(objectMapper.writeValueAsString(testEventUpdateRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        testEventUpdateRequestDto.setStateAction(EventUpdateState.CANCEL_REVIEW);

        when(mockPrivateService.updateEvent(anyLong(), anyLong(), any()))
                .thenReturn(testEventResponseDto);
        mockMvc.perform(patch("/users/1/events/1")
                        .content(objectMapper.writeValueAsString(testEventUpdateRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    //requests
    @Test
    public void testAddRequest() throws Exception {
        when(mockPrivateService.addRequest(anyLong(), anyLong()))
                .thenReturn(testEventRequestResponseDto);
        mockMvc.perform(post("/users/1/requests?eventId=1"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetRequestsForEvent() throws Exception {
        when(mockPrivateService.getRequestsForEvent(anyLong(), anyLong()))
                .thenReturn(List.of(testEventRequestResponseDto, testEventRequestResponseDto));
        mockMvc.perform(get("/users/1/events/1/requests"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testGetRequestsForUser() throws Exception {
        when(mockPrivateService.getRequestsForUser(anyLong()))
                .thenReturn(List.of(testEventRequestResponseDto, testEventRequestResponseDto));
        mockMvc.perform(get("/users/1/requests"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testModerateRequests() throws Exception {
        mockMvc.perform(patch("/users/1/events/1/requests")
                    .content(objectMapper.writeValueAsString(
                            new EventRequestUpdateRequestDto(null, EventRequestStatus.CONFIRMED))
                    )
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mockMvc.perform(patch("/users/1/events/1/requests")
                        .content(objectMapper.writeValueAsString(
                                new EventRequestUpdateRequestDto(List.of(1L, 2L), null))
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mockMvc.perform(patch("/users/1/events/1/requests")
                        .content(objectMapper.writeValueAsString(
                                new EventRequestUpdateRequestDto(List.of(1L, 2L), EventRequestStatus.PENDING))
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        when(mockPrivateService.moderateRequests(anyLong(), anyLong(), any()))
                .thenReturn(testEventRequestUpdateResponseDto);
        mockMvc.perform(patch("/users/1/events/1/requests")
                    .content(objectMapper.writeValueAsString(testEventRequestUpdateRequestDto))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testCancelRequest() throws Exception {
        when(mockPrivateService.cancelRequest(anyLong(), anyLong()))
                .thenReturn(testEventRequestResponseDto);
        mockMvc.perform(patch("/users/1/requests/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
