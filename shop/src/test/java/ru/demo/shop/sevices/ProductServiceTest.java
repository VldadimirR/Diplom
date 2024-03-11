package ru.demo.shop.sevices;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.demo.shop.dao.ProductDao;
import ru.demo.shop.exception.ProductNotFoundException;
import ru.demo.shop.models.Product;
import ru.demo.shop.repositories.ProductRepository;
import ru.demo.shop.request.ProductUpdateRequest;
import ru.demo.shop.services.ProductService;

import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ProductServiceTest {

    @Mock
    private ProductDao productDao;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testGetAllProducts() {
        // Given
        Product product1 = new Product();
        product1.setName("Test Product 1");
        product1.setDescription("Description for Test Product 1");
        product1.setPrice(19.99);

        Product product2 = new Product();
        product2.setName("Test Product 2");
        product2.setDescription("Description for Test Product 2");
        product2.setPrice(29.99);

        productRepository.saveAll(List.of(product1, product2));

        // When
        List<Product> products = productService.getAllProducts();

        // Then
        assertNotNull(products);
        assertEquals(2, products.size());
    }

    @Test
    public void testGetProductById() {
        // Given
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Description for Test Product");
        product.setPrice(39.99);
        Product savedProduct = productRepository.save(product);

        // When
        Optional<Product> retrievedProduct = productService.getProduct(savedProduct.getId());

        // Then
        assertTrue(retrievedProduct.isPresent());
        assertEquals(savedProduct.getId(), retrievedProduct.get().getId());
    }

    @Test
    public void testGetCustomerThrowsExceptionWhenNotFound() {
        // Given
        Long productId = 1L;
        when(productDao.selectProductById(productId)).thenReturn(Optional.empty());

        // When, Then
        assertThatThrownBy(() ->
                productService.getProduct(productId))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("product with id [%s] not found".formatted(productId));
    }

    @Test
    public void testAddProduct() {
        // Given
        Product product = new Product();
        product.setName("New Product");
        product.setDescription("Description for New Product");
        product.setPrice(49.99);

        // When
        productService.addProduct(product);

        // Then
        Product addedProduct = productService.getProduct(product.getId()).orElse(null);

        assertNotNull(addedProduct);
        assertNotNull(addedProduct.getId());
        assertEquals(product.getName(), addedProduct.getName());
        assertEquals(product.getDescription(), addedProduct.getDescription());
        assertEquals(product.getPrice(), addedProduct.getPrice());
    }

    @Test
    public void testUpdateProduct() {
        // Given
        Product product = new Product();
        product.setName("Original Product");
        product.setDescription("Description for Original Product");
        product.setPrice(59.99);
        Product savedProduct = productRepository.save(product);

        // When
        savedProduct.setName("Updated Product");
        savedProduct.setDescription("Updated Description");
        savedProduct.setPrice(69.99);
//        productService.updateProduct(savedProduct.getId(), savedProduct);

        // Then
        Product updatedProduct = productService.getProduct(savedProduct.getId()).orElse(null);
        assertNotNull(updatedProduct);
        assertEquals(savedProduct.getId(), updatedProduct.getId());
        assertEquals(savedProduct.getName(), updatedProduct.getName());
        assertEquals(savedProduct.getDescription(), updatedProduct.getDescription());
        assertEquals(savedProduct.getPrice(), updatedProduct.getPrice());
    }

    @Test
    public void testUpdateProductWithOnlyName() {
        // Given
        Product originalProduct = new Product();
        originalProduct.setName("Original Name");
        originalProduct.setDescription("Original Description");
        originalProduct.setPrice(19.99);

        productRepository.save(originalProduct);

        // When
        ProductUpdateRequest updatedProduct = new ProductUpdateRequest(
                "Updated Name",
                null,
                null,
                null);

        productService.updateProduct(originalProduct.getId(), updatedProduct);

        // Then
        Product resultProduct = productService.getProduct(originalProduct.getId()).orElse(null);
        assertNotNull(resultProduct);

        // Проверяем, что обновлено только поле "Name"
        assertEquals(updatedProduct.name(), resultProduct.getName());
        assertEquals(originalProduct.getDescription(), resultProduct.getDescription());
        assertEquals(originalProduct.getPrice(), resultProduct.getPrice());
    }

    @Test
    public void testUpdateProductWithOnlyDescription() {
        // Given
        Product originalProduct = new Product();
        originalProduct.setName("Original Name");
        originalProduct.setDescription("Original Description");
        originalProduct.setPrice(19.99);

        productRepository.save(originalProduct);

        // When
        ProductUpdateRequest updatedProduct = new ProductUpdateRequest(
                null,
                "Updated Description",
                null,
                null);

        productService.updateProduct(originalProduct.getId(), updatedProduct);

        // Then
        Product resultProduct = productService.getProduct(originalProduct.getId()).orElse(null);
        assertNotNull(resultProduct);

        // Проверяем, что обновлено только поле "Description"
        assertEquals(originalProduct.getName(), resultProduct.getName());
        assertEquals(updatedProduct.description(), resultProduct.getDescription());
        assertEquals(originalProduct.getPrice(), resultProduct.getPrice());
    }

    @Test
    public void testUpdateProductWithOnlyPrice() {
        // Given
        Product originalProduct = new Product();
        originalProduct.setName("Original Name");
        originalProduct.setDescription("Original Description");
        originalProduct.setPrice(19.99);

        productRepository.save(originalProduct);

        // When
        ProductUpdateRequest updatedProduct = new ProductUpdateRequest(
                null,
                null,
                29.99,
                null);

        productService.updateProduct(originalProduct.getId(), updatedProduct);

        // Then
        Product resultProduct = productService.getProduct(originalProduct.getId()).orElse(null);
        assertNotNull(resultProduct);

        // Проверяем, что обновлено только поле "Price"
        assertEquals(originalProduct.getName(), resultProduct.getName());
        assertEquals(originalProduct.getDescription(), resultProduct.getDescription());
        assertEquals(updatedProduct.price(), resultProduct.getPrice());
    }



    @Test
    public void testDeleteProduct() {
        // Given
        Product product = new Product();
        product.setName("Product to Delete");
        product.setDescription("Description for Product to Delete");
        product.setPrice(79.99);
        Product savedProduct = productRepository.save(product);

        // When
        productService.deleteProduct(savedProduct.getId());

        // Then
        Optional<Product> deletedProduct = productRepository.findById(savedProduct.getId());
        assertFalse(deletedProduct.isPresent());
    }

    @Test
    public void willThrowDeleteProductByIdNotExists() {
        // Given
        Long productId = 1L;
        when(productDao.existsProductWithId(productId)).thenReturn(false);

        // When
        assertThatThrownBy(() ->
                productService.deleteProduct(productId))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("product with id [%s] not found"
                        .formatted(productId));
        // Then
        verify(productDao, never()).deleteProduct(productId);
    }

}
