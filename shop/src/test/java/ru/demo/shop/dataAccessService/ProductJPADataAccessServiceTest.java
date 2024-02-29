package ru.demo.shop.dataAccessService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.demo.shop.models.Product;
import ru.demo.shop.repositories.ProductRepository;
import ru.demo.shop.services.ProductJPADataAccessService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductJPADataAccessServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductJPADataAccessService productService;

    @Test
    void selectAllProducts() {
        // Arrange
        List<Product> products = Arrays.asList(new Product(), new Product());
        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<Product> result = productService.selectAllProducts();

        // Assert
        assertEquals(products, result);
    }

    @Test
    void selectProductById() {
        // Arrange
        Long productId = 1L;
        Product product = new Product();
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act
        Optional<Product> result = productService.selectProductById(productId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(product, result.get());
    }

    @Test
    void insertProduct() {
        // Arrange
        Product product = new Product();

        // Act
        productService.insertProduct(product);

        // Assert
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void updateProduct() {
        // Arrange
        Product updatedProduct = new Product();

        // Act
        productService.updateProduct(updatedProduct);

        // Assert
        verify(productRepository, times(1)).save(updatedProduct);
    }

    @Test
    void deleteProduct() {
        // Arrange
        Long productId = 1L;

        // Act
        productService.deleteProduct(productId);

        // Assert
        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    void existsProductWithId() {
        // Arrange
        Long productId = 1L;
        when(productRepository.existsProductById(productId)).thenReturn(true);

        // Act
        boolean result = productService.existsProductWithId(productId);

        // Assert
        assertTrue(result);
    }
}
