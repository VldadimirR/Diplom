package ru.demo.shop.contollers;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.demo.shop.exception.ProductNotFoundException;
import ru.demo.shop.models.Product;
import ru.demo.shop.request.ProductUpdateRequest;
import ru.demo.shop.services.OrderService;
import ru.demo.shop.services.ProductService;
import ru.demo.shop.services.UserService;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetAdminPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/adminPage"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("page/adminPage"));
    }

    @Test
    @WithMockUser(username = "testadmin", roles = {"ADMIN"})
    public void testGetAllProductsPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/admin-products"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("page/admin/all-products"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("products"));
    }

    @Test
    void testGetProductAddPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/admin-products-add"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("page/admin/add-product"));
    }

    @Test
    void testGetUsersPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("page/admin/users"));
    }

    @Test
    void testGetOrdersPage() throws Exception {
        // Выполнение GET-запроса и проверка ожидаемого результата
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/orders"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("page/admin/orders"));
    }


    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testGetPage_UserNotFoundException() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/page"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("page/admin/exceptionNotFound"));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


}
