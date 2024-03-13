package ru.demo.shop.contollers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.demo.shop.exception.UserNotFoundException;
import ru.demo.shop.models.Role;
import ru.demo.shop.models.User;
import ru.demo.shop.request.UserUpdateRequest;
import ru.demo.shop.services.OrderService;
import ru.demo.shop.services.UserService;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @Test
    public void testGetAllUsers() throws Exception {
        List<User> users = new ArrayList<>();
        User user = new User("User1",
                "user1@example.com",
                "123456789",
                "Address1",
                "password",
                Role.ROLE_USER);

        User user2 = new User(
                "User2",
                "user2@example.com",
                "987654321",
                "Address2",
                "password",
                Role.ROLE_USER);

        users.add(user);
        users.add(user2);

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstname").value("User1"))
                .andExpect(jsonPath("$[0].email").value("user1@example.com"))
                .andExpect(jsonPath("$[0].phone").value("123456789"))
                .andExpect(jsonPath("$[0].address").value("Address1"))
                .andExpect(jsonPath("$[0].password").value("password"))
                .andExpect(jsonPath("$[0].role").value("ROLE_USER"));

    }

    @Test
    public void testGetUserById_Exists() throws Exception {
        long userId = 1L;
        User user = new User(
                userId,
                "User1",
                "user1@example.com",
                "123456789",
                "Address1",
                "password",
                Role.ROLE_USER);

        when(userService.getUser(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.firstname").value("User1"))
                .andExpect(jsonPath("$.email").value("user1@example.com"))
                .andExpect(jsonPath("$.phone").value("123456789"))
                .andExpect(jsonPath("$.address").value("Address1"))
                .andExpect(jsonPath("$.password").value("password"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    public void testGetUserById_NotFound() throws Exception {
        long userId = 1L;

        when(userService.getUser(userId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateUser_Success() throws Exception {
        long userId = 1L;
        UserUpdateRequest updatedUser = new UserUpdateRequest(
                "UpdatedUser",
                "updateduser@example.com",
                "987654321",
                "UpdatedAddress",
                Role.ROLE_ADMIN);

        mockMvc.perform(put("/api/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateUser_UserNotFound() throws Exception {
        long userId = 1L;
        UserUpdateRequest updatedUser = new UserUpdateRequest(
                "UpdatedUser",
                "updateduser@example.com",
                "987654321",
                "UpdatedAddress",
                Role.ROLE_ADMIN);

        doThrow(UserNotFoundException.class).when(userService).updateUser(userId, updatedUser);

        mockMvc.perform(put("/api/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteUser_Success() throws Exception {
        long userId = 1L;

        mockMvc.perform(delete("/api/users/" + userId))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(userId);
    }

    @Test
    public void testDeleteUser_UserNotFound() throws Exception {
        long userId = 1L;

        doThrow(UserNotFoundException.class).when(userService).deleteUser(userId);

        mockMvc.perform(delete("/api/users/" + userId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetUsersByRole() throws Exception {
        Map<String, Integer> usersByRole = new HashMap<>();
        usersByRole.put("ROLE_USER", 10);
        usersByRole.put("ROLE_ADMIN", 5);

        when(userService.getUsersByRole()).thenReturn(usersByRole);

        mockMvc.perform(get("/api/users/roles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ROLE_USER").value(10))
                .andExpect(jsonPath("$.ROLE_ADMIN").value(5));
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testGetUserPage() throws Exception {
        User testUser = new User(
                "testUser",
                "test@example.com",
                "123456789",
                "Test Address",
                "password",
                Role.ROLE_USER);

        Mockito.when(userService.getCurrentUser()).thenReturn(Optional.of(testUser));
        Mockito.when(userService.getCurrentUserRole()).thenReturn(Role.ROLE_USER);
        Mockito.when(orderService.getOrdersByUserId(anyLong())).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/userPage"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("page/userPage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("role"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("orders"));
    }
}
