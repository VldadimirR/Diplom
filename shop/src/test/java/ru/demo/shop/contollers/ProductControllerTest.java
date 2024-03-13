package ru.demo.shop.contollers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import ru.demo.shop.exception.ProductNotFoundException;
import ru.demo.shop.models.Product;
import ru.demo.shop.request.ProductUpdateRequest;
import ru.demo.shop.services.ProductService;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    public void testGetProductById_Exists() throws Exception {
        long productId = 1L;
        Product product = new Product(productId,
                "Test Product 1",
                "Description for Test Product 1",
                19.99,
                "category for original"
        );
        when(productService.getProduct(productId)).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value("Test Product 1"))
                .andExpect(jsonPath("$.description").value("Description for Test Product 1"))
                .andExpect(jsonPath("$.price").value(19.99))
                .andExpect(jsonPath("$.category").value("category for original"));
    }

    @Test
    public void testGetProductById_NotFound() throws Exception {
        long productId = 1L;
        when(productService.getProduct(productId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/" + productId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateProduct() throws Exception {
        Product product = new Product(
                1L,
                "Test Product",
                "Description for Test Product",
                19.99,
                "category"
        );

        mockMvc.perform(post("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(product)))
                .andExpect(status().isCreated());

        verify(productService, times(1)).addProduct(any(Product.class));
    }

    @Test
    public void testUpdateProduct_Success() throws Exception {
        long productId = 1L;
        ProductUpdateRequest updateRequest = new ProductUpdateRequest(
                "Updated Product Name",
                "Updated Product Description",
                29.99,
                "Updated Category"
        );

        mockMvc.perform(put("/api/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updateRequest)))
                .andExpect(status().isOk());

        verify(productService, times(1)).updateProduct(productId, updateRequest);
    }

    @Test
    public void testUpdateProduct_NotFound() throws Exception {
        long productId = 1L;
        ProductUpdateRequest updateRequest = new ProductUpdateRequest(
                "Updated Product Name",
                "Updated Product Description",
                29.99,
                "Updated Category"
        );

        doThrow(new ProductNotFoundException("")).when(productService).updateProduct(productId, updateRequest);

        mockMvc.perform(put("/api/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteProduct_Success() throws Exception {
        long productId = 1L;

        mockMvc.perform(delete("/api/" + productId))
                .andExpect(status().isOk());

        verify(productService, times(1)).deleteProduct(productId);
    }

    @Test
    public void testDeleteProduct_NotFound() throws Exception {
        long productId = 1L;

        doThrow(new ProductNotFoundException("")).when(productService).deleteProduct(productId);

        mockMvc.perform(delete("/" + productId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetProductPrices_Success() throws Exception {
        Map<String, Integer> priceDistribution = new HashMap<>();
        priceDistribution.put("Product1", 100);
        priceDistribution.put("Product2", 200);

        when(productService.getProductPriceDistribution()).thenReturn(priceDistribution);

        mockMvc.perform(get("/api/price"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Product1").value(100))
                .andExpect(jsonPath("$.Product2").value(200));
    }

    @Test
    public void testGetProductPrices_InternalServerError() throws Exception {
        when(productService.getProductPriceDistribution()).thenThrow(new RuntimeException("Test Exception"));

        mockMvc.perform(get("/api/price"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$").value("Error fetching product prices"));
    }

    @Test
    public void testGetProductCountByCategory() throws Exception {
        Map<String, Long> productCountByCategory = new HashMap<>();
        productCountByCategory.put("Category1", 10L);
        productCountByCategory.put("Category2", 20L);

        when(productService.getProductCountByCategory()).thenReturn(productCountByCategory);

        mockMvc.perform(get("/api/count-by-category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Category1").value(10))
                .andExpect(jsonPath("$.Category2").value(20));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testGetAllProducts() throws Exception {
        // Arrange
        String sort = "priceLowToHigh";
        String category = "Electronics";
        String productName = "iPhone";
        int page = 0;
        int size = 6;

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products")
                        .param("sort", sort)
                        .param("category", category)
                        .param("productName", productName)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("catalog"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("products"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("categories"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentPage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("pageSize"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("totalPages"));
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
