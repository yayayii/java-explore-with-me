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
import ru.practicum.explorewithme.dto.category.CategoryRequestDto;
import ru.practicum.explorewithme.dto.category.CategoryResponseDto;
import ru.practicum.explorewithme.dto.compilation.CompilationRequestDto;
import ru.practicum.explorewithme.dto.compilation.CompilationResponseDto;
import ru.practicum.explorewithme.dto.event.EventAdminUpdateRequestDto;
import ru.practicum.explorewithme.dto.event.EventResponseDto;
import ru.practicum.explorewithme.dto.event.EventShortResponseDto;
import ru.practicum.explorewithme.dto.event.LocationDto;
import ru.practicum.explorewithme.dto.user.UserRequestDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.model.event.EventState;
import ru.practicum.explorewithme.model.event.EventUpdateState;
import ru.practicum.explorewithme.service.AdminService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {
    @Mock
    private AdminService mockAdminService;
    @Mock
    private StatClient mockStatClient;
    @InjectMocks
    private AdminController adminController;
    private static ObjectMapper objectMapper;
    private MockMvc mockMvc;

    private static CategoryRequestDto testCategoryRequestDto;
    private static CategoryResponseDto testCategoryResponseDto;
    private static CompilationRequestDto testCompilationRequestDto;
    private static CompilationResponseDto testCompilationResponseDto;
    private static LocalDateTime testLocalDateTime;
    private static EventAdminUpdateRequestDto testEventAdminUpdateRequestDto;
    private static EventShortResponseDto testEventShortResponseDto;
    private static EventResponseDto testEventResponseDto;
    private static UserRequestDto testUserRequestDto;
    private static UserResponseDto testUserResponseDto;


    @BeforeAll
    public static void beforeAll() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testCategoryRequestDto = new CategoryRequestDto("name1");
        testCategoryResponseDto = new CategoryResponseDto(1L, "name1");

        testUserRequestDto = new UserRequestDto("email1@email.ru", "name1");
        testUserResponseDto = new UserResponseDto(1L, "email1@email.ru", "name1");

        testLocalDateTime = LocalDateTime.of(2024, 1, 1, 1, 1);
        testEventAdminUpdateRequestDto = new EventAdminUpdateRequestDto(
                "newTitle1", "newAnnotation11111111", "newDescription1111111",
                false, false, 1, testLocalDateTime,
                new LocationDto(0.0, 0.0), 1L, EventUpdateState.PUBLISH_EVENT
        );
        testEventShortResponseDto = new EventShortResponseDto(
                1L, "title1", "annotation1", false, testCategoryResponseDto, 1,
                testLocalDateTime, 1, testUserResponseDto
        );
        testEventResponseDto = new EventResponseDto(
                1L, "title1", "annotation1", "description1", false,
                false, testCategoryResponseDto, 1, 1,
                testLocalDateTime, testLocalDateTime, testLocalDateTime, new LocationDto(1.1, 1.1), 1,
                testUserResponseDto, EventState.PUBLISHED
        );

        testCompilationRequestDto = new CompilationRequestDto("title1", false, new Long[]{1L, 2L});
        testCompilationResponseDto = new CompilationResponseDto(
                1L, "title1", false, List.of(testEventShortResponseDto, testEventShortResponseDto)
        );
    }

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }


    //categories
    @Test
    public void testAddCategory() throws Exception {
        mockMvc.perform(post("/admin/categories")
                        .content(objectMapper.writeValueAsString(new CategoryRequestDto("")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        when(mockAdminService.addCategory(any()))
                .thenReturn(testCategoryResponseDto);
        mockMvc.perform(post("/admin/categories")
                        .content(objectMapper.writeValueAsString(testCategoryRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testUpdateCategory() throws Exception {
        mockMvc.perform(patch("/admin/categories/1")
                        .content(objectMapper.writeValueAsString(new CategoryRequestDto("")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        when(mockAdminService.updateCategory(anyLong(), any()))
                .thenReturn(testCategoryResponseDto);
        mockMvc.perform(patch("/admin/categories/1")
                        .content(objectMapper.writeValueAsString(testCategoryRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testDeleteCategory() throws Exception {
        doNothing().when(mockAdminService).deleteCategory(anyLong());
        mockMvc.perform(delete("/admin/categories/1"))
                .andExpect(status().isNoContent());
    }

    //compilations
    @Test
    public void testAddCompilation() throws Exception {
        testCompilationRequestDto.setTitle(null);
        mockMvc.perform(post("/admin/compilations")
                    .content(objectMapper.writeValueAsString(testCompilationRequestDto))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        testCompilationRequestDto.setTitle("title1");

        when(mockAdminService.addCompilation(any()))
                .thenReturn(testCompilationResponseDto);
        mockMvc.perform(post("/admin/compilations")
                        .content(objectMapper.writeValueAsString(testCompilationRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testUpdateCompilation() throws Exception {
        when(mockAdminService.updateCompilation(anyLong(), any()))
                .thenReturn(testCompilationResponseDto);
        mockMvc.perform(patch("/admin/compilations/1")
                        .content(objectMapper.writeValueAsString(testCompilationRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testDeleteCompilation() throws Exception {
        doNothing().when(mockAdminService).deleteCompilation(anyLong());
        mockMvc.perform(delete("/admin/compilations/1"))
                .andExpect(status().isNoContent());
    }

    //events
    @Test
    public void testSearchEvents() throws Exception {
        when(mockAdminService.searchEvents(any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(testEventResponseDto, testEventResponseDto));
        mockMvc.perform(get("/admin/events?users=1,2&states=PUBLISHED,PENDING&categories=1,2&rangeStart=2023-01-01 12:12:12&rangeEnd=2023-01-01 12:12:12&from=1&size=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
        mockMvc.perform(get("/admin/events"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testUpdateAdminEvent() throws Exception {
        testEventAdminUpdateRequestDto.setStateAction(null);
        mockMvc.perform(patch("/admin/events/1")
                        .content(objectMapper.writeValueAsString(testEventAdminUpdateRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        testEventAdminUpdateRequestDto.setStateAction(EventUpdateState.PUBLISH_EVENT);

        when(mockAdminService.updateAdminEvent(anyLong(), any()))
                .thenReturn(testEventResponseDto);
        mockMvc.perform(patch("/admin/events/1")
                        .content(objectMapper.writeValueAsString(testEventAdminUpdateRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    //users
    @Test
    public void testAddUser() throws Exception {
        mockMvc.perform(post("/admin/users")
                        .content(objectMapper.writeValueAsString(new UserRequestDto("", "name1")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/admin/users")
                        .content(objectMapper.writeValueAsString(new UserRequestDto("email1", "name1")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/admin/users")
                        .content(objectMapper.writeValueAsString(new UserRequestDto("email1@email.ru", "")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        when(mockAdminService.addUser(any()))
                .thenReturn(testUserResponseDto);
        mockMvc.perform(post("/admin/users")
                        .content(objectMapper.writeValueAsString(testUserRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetUsers() throws Exception {
        when(mockAdminService.getUsers(anyInt(), anyInt()))
                .thenReturn(List.of(testUserResponseDto, testUserResponseDto));
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testDeleteUser() throws Exception {
        doNothing().when(mockAdminService).deleteUser(anyLong());
        mockMvc.perform(delete("/admin/users/1"))
                .andExpect(status().isNoContent());
    }
}
