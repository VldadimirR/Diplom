package ru.demo.shop.dataAccessService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.demo.shop.models.Product;
import ru.demo.shop.repositories.ProductRepository;
import ru.demo.shop.services.ProductJPADataAccessService;

import java.util.ArrayList;
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

    @Test
    public void testGetProductsSortedByPriceAsc() {
        // Given
        List<Product> mockProducts = Arrays.asList(
                new Product(1L, "Product 1", "Description1", 50.0, "Category1", 2),
                new Product(2L, "Product 2", "Description2", 30.0, "Category2", 2),
                new Product(3L, "Product 3", "Description3", 70.0, "Category3", 1)
        );

        // When
        List<Product> sortedProducts = productService.getProductsSortedByPriceAsc(mockProducts);

        // Then
        assertEquals("Product 2", sortedProducts.get(0).getName());
        assertEquals("Product 1", sortedProducts.get(1).getName());
        assertEquals("Product 3", sortedProducts.get(2).getName());
    }

    @Test
    public void testGetProductsSortedByPriceDesc() {
        // Given
        List<Product> mockProducts = Arrays.asList(
                new Product(1L, "Product 1", "Description1", 50.0, "Category1", 2),
                new Product(2L, "Product 2", "Description2", 30.0, "Category2", 2),
                new Product(3L, "Product 3", "Description3", 70.0, "Category3", 1)
        );

        // When
        List<Product> sortedProducts = productService.getProductsSortedByPriceDesc(mockProducts);

        // Then
        assertEquals("Product 3", sortedProducts.get(0).getName());
        assertEquals("Product 1", sortedProducts.get(1).getName());
        assertEquals("Product 2", sortedProducts.get(2).getName());
    }

    @Test
    public void testGetProductsByCategory() {
        // Given
        List<Product> expectedProducts = new ArrayList<>();
        expectedProducts.add(new Product("Product 1", "Description 1", 50.0, "Category"));
        expectedProducts.add(new Product("Product 2", "Description 2", 30.0, "Category"));

        when(productRepository.findByCategory("Category")).thenReturn(expectedProducts);

        // When
        List<Product> actualProducts = productService.getProductsByCategory("Category");

        // Then
        assertEquals(expectedProducts, actualProducts);
        verify(productRepository, times(1)).findByCategory("Category");
    }

    @Test
    public void testGetProductsByName() {
        // Given
        List<Product> expectedProducts = new ArrayList<>();
        expectedProducts.add(new Product("Product 1", "Description 1", 50.0, "Category"));
        expectedProducts.add(new Product("Product 2", "Description 2", 30.0, "Category"));

        when(productRepository.findByNameContainingIgnoreCase("Product")).thenReturn(expectedProducts);

        // When
        List<Product> actualProducts = productService.getProductsByName("Product");

        // Then
        assertEquals(expectedProducts, actualProducts);
        verify(productRepository, times(1)).findByNameContainingIgnoreCase("Product");
    }

    @Test
    public void testGetAllCategories() {
        // Given
        List<String> expectedCategories = new ArrayList<>();
        expectedCategories.add("Category1");
        expectedCategories.add("Category2");

        when(productRepository.getAllCategory()).thenReturn(expectedCategories);

        // When
        List<String> actualCategories = productService.getAllCategories();

        // Then
        assertEquals(expectedCategories, actualCategories);
        verify(productRepository, times(1)).getAllCategory();
    }

    @Test
    public void testCountProductsByPriceLessThan() {
        // Given
        int expectedCount = 5;

        when(productRepository.countProductsByPriceLessThan(100.0)).thenReturn(expectedCount);

        // When
        int actualCount = productService.countProductsByPriceLessThan(100.0);

        // Then
        assertEquals(expectedCount, actualCount);
        verify(productRepository, times(1)).countProductsByPriceLessThan(100.0);
    }

    @Test
    public void testCountProductsByPriceBetween() {
        // Given
        int expectedCount = 10;

        when(productRepository.countProductsByPriceBetween(50.0, 100.0)).thenReturn(expectedCount);

        // When
        int actualCount = productService.countProductsByPriceBetween(50.0, 100.0);

        // Then
        assertEquals(expectedCount, actualCount);
        verify(productRepository, times(1)).countProductsByPriceBetween(50.0, 100.0);
    }

    @Test
    public void testCountProductsByPriceGreaterThanEqual() {
        // Given
        int expectedCount = 8;

        when(productRepository.countProductsByPriceGreaterThanEqual(200.0)).thenReturn(expectedCount);

        // When
        int actualCount = productService.countProductsByPriceGreaterThanEqual(200.0);

        // Then
        assertEquals(expectedCount, actualCount);
        verify(productRepository, times(1)).countProductsByPriceGreaterThanEqual(200.0);
    }

    @Test
    public void testGetProductCountByCategory() {
        // Given
        Object[] category1Count = new Object[]{"Category1", 10L};
        Object[] category2Count = new Object[]{"Category2", 15L};

        when(productRepository.getProductCountByCategory())
                .thenReturn(List.of(category1Count, category2Count));

        // When
        List<Object[]> result = productService.getProductCountByCategory();

        // Then
        assertEquals(2, result.size());
        assertEquals(category1Count[0], result.get(0)[0]);
        assertEquals(category1Count[1], result.get(0)[1]);
        assertEquals(category2Count[0], result.get(1)[0]);
        assertEquals(category2Count[1], result.get(1)[1]);
        verify(productRepository, times(1)).getProductCountByCategory();
    }

}
