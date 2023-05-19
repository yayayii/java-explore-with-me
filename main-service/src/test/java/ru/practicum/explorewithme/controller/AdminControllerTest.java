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
import ru.practicum.explorewithme.dto.category.CategoryRequestDto;
import ru.practicum.explorewithme.dto.category.CategoryResponseDto;
import ru.practicum.explorewithme.dto.user.UserRequestDto;
import ru.practicum.explorewithme.dto.user.UserResponseDto;
import ru.practicum.explorewithme.service.AdminService;

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
    @InjectMocks
    private AdminController adminController;
    private static ObjectMapper objectMapper;
    private MockMvc mockMvc;

    private static CategoryRequestDto testCategoryRequestDto;
    private static CategoryResponseDto testCategoryResponseDto;
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
        mockMvc.perform(get("/admin/users?from=qwe"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/admin/users?size=qwe"))
                .andExpect(status().isBadRequest());

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