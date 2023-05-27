package ru.practicum.explorewithme.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.practicum.explorewithme.dto.user.UserRequestDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.service.admin.AdminUserService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AdminUserControllerTest {
    @Mock
    private AdminUserService mockAdminService;
    @InjectMocks
    private AdminUserController adminController;
    private static ObjectMapper objectMapper;
    private MockMvc mockMvc;

    private static final String API_PREFIX = "/admin/users";
    private static UserRequestDto testUserRequestDto;
    private static UserResponseDto testUserResponseDto;


    @BeforeAll
    public static void beforeAll() {
        objectMapper = new ObjectMapper();

        testUserRequestDto = new UserRequestDto("email1@email.ru", "name1");
        testUserResponseDto = new UserResponseDto(1L, "email1@email.ru", "name1");
    }

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }


    //users
    @Test
    public void testAddUser() throws Exception {
        mockMvc.perform(post(API_PREFIX)
                        .content(objectMapper.writeValueAsString(new UserRequestDto("", "name1")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post(API_PREFIX)
                        .content(objectMapper.writeValueAsString(new UserRequestDto("email1", "name1")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post(API_PREFIX)
                        .content(objectMapper.writeValueAsString(new UserRequestDto("email1@email.ru", "")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        when(mockAdminService.addUser(any()))
                .thenReturn(testUserResponseDto);
        mockMvc.perform(post(API_PREFIX)
                        .content(objectMapper.writeValueAsString(testUserRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetUsers() throws Exception {
        when(mockAdminService.getUsers(any(), anyInt(), anyInt()))
                .thenReturn(List.of(testUserResponseDto, testUserResponseDto));
        mockMvc.perform(get(API_PREFIX))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testDeleteUser() throws Exception {
        doNothing().when(mockAdminService).deleteUser(anyLong());
        mockMvc.perform(delete(API_PREFIX + "/1"))
                .andExpect(status().isNoContent());
    }
}
