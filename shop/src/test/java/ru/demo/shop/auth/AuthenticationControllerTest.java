package ru.demo.shop.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testShowRegistrationForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/register"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("login/registration"));
    }

    @Test
    public void testLoginPage() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(view().name("login/loginPage"));
    }

    @Test
    public void testRegister() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .param("firstname", "John")
                        .param("email", "john@example.com")
                        .param("phone", "42424234234")
                        .param("address", "Address")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    public void testRegisterException() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .param("firstname", "John")
                        .param("email", "john@example.com")
                        .param("phone", "42424234234")
                        .param("address", "Address")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/register?error=emailAlreadyInUse"));
    }

    @Test
    @WithMockUser(username = "john@example.com")
    public void testSuccessfulLogin() throws Exception {
        mockMvc.perform(post("/process_login")
                        .param("username", "john@example.com")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/index"));
    }

    @Test
    public void testFailedLogin() throws Exception {
        mockMvc.perform(post("/process_login")
                        .param("username", "invaliduser")
                        .param("password", "invalidpassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?error"));
    }

    @Test
    @WithMockUser(username = "john@example.com")
    public void testSuccessfulLoginCreatesNewSession() throws Exception {
        // Given
        MockHttpSession session = new MockHttpSession();

        // When
        mockMvc.perform(MockMvcRequestBuilders.post("/process_login")
                        .session(session)
                        .param("username", "john@example.com")
                        .param("password", "password"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/api/index"));

        // Then
        assertNotNull(session.getId());
    }

    @Test
    @WithMockUser
    public void testLogout() throws Exception {
        // Отправляем GET запрос на эндпоинт /logout
        mockMvc.perform(get("/auth/logout"))
                // Проверяем, что возвращается статус перенаправления (302)
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                // Проверяем, что происходит перенаправление на /login
                .andExpect(MockMvcResultMatchers.redirectedUrl("/auth/login"));

        // Попытка доступа к защищенному ресурсу после выхода из системы
        mockMvc.perform(get("/api/users/userPage"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection()) // Ожидаем перенаправление
                .andExpect(MockMvcResultMatchers.redirectedUrl("http://localhost/auth/login")); // Проверяем перенаправление на страницу входа


    }
}