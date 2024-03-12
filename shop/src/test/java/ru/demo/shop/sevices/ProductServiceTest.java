package ru.demo.shop.sevices;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
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

import java.util.*;

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

    @InjectMocks
    private ProductService productService;

    @Test
    public void testGetAllProducts() {
        // Given
        Product product1 = new Product("Test Product 1",
                "Description for Test Product 1",
                19.99,
                "category for original"
                );

        Product product2 = new Product("Test Product 2",
                "Description for Test Product 2",
                29.99,
                "category for original"
                );

        // When
        when(productDao.selectAllProducts()).thenReturn(List.of(product1,product2));

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

        long id = 1;

        // When
        when(productDao.selectProductById(id)).thenReturn(Optional.of(product));

        Optional<Product> retrievedProduct = productService.getProduct(id);

        // Then
        assertTrue(retrievedProduct.isPresent());
        assertEquals(product.getId(), retrievedProduct.get().getId());
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

        long id = 1;

        when(productDao.selectProductById(id)).thenReturn(Optional.of(product));

        // When
        productService.addProduct(product);

        // Then
        Product addedProduct = productService.getProduct(id).orElse(null);

        assertNotNull(addedProduct);
        assertEquals(product.getName(), addedProduct.getName());
        assertEquals(product.getDescription(), addedProduct.getDescription());
        assertEquals(product.getPrice(), addedProduct.getPrice());
    }

    @Test
    public void testUpdateProduct() {
        // Given
        Product product = new Product("Original Product",
                "Description for Original Product",
                59.99,
                "category for original");

        ProductUpdateRequest productUpdateRequest = new ProductUpdateRequest(
                "Updated Product",
                "Updated Description",
                69.99,
                "category");

        long id = 1;

        when(productDao.selectProductById(id)).thenReturn(Optional.of(product));

        // When
        productService.updateProduct(id, productUpdateRequest);

        // Then
        Product updatedProduct = productService.getProduct(id).orElse(null);

        assertNotNull(updatedProduct);
        assertEquals(updatedProduct.getId(), product.getId());
        assertEquals(updatedProduct.getName(), product.getName());
        assertEquals(updatedProduct.getDescription(), product.getDescription());
        assertEquals(updatedProduct.getPrice(), product.getPrice());
        assertEquals(updatedProduct.getCategory(), product.getCategory());
    }

    @Test
    public void testUpdateProductWithOnlyName() {
        // Given
        Product product = new Product("Original Product",
                "Description for Original Product",
                59.99,
                "category for original");

        ProductUpdateRequest productUpdateRequest = new ProductUpdateRequest(
                "Updated Product",
                "Updated Description",
                69.99,
                "category");

        long id = 1;

        when(productDao.selectProductById(id)).thenReturn(Optional.of(product));

        // When
        productService.updateProduct(id, productUpdateRequest);

        // Then
        Product updatedProduct = productService.getProduct(id).orElse(null);

        assertNotNull(updatedProduct);

        assertEquals(productUpdateRequest.name(), updatedProduct.getName());
        assertEquals(product.getDescription(), updatedProduct.getDescription());
        assertEquals(product.getPrice(), updatedProduct.getPrice());
    }

    @Test
    public void testUpdateProductWithOnlyDescription() {
        // Given
        Product product = new Product("Original Product",
                "Description for Original Product",
                59.99,
                "category for original");

        ProductUpdateRequest productUpdateRequest = new ProductUpdateRequest(
                "Updated Product",
                "Updated Description",
                69.99,
                "category");

        long id = 1;

        when(productDao.selectProductById(id)).thenReturn(Optional.of(product));

        // When
        productService.updateProduct(id, productUpdateRequest);

        // Then
        Product updatedProduct = productService.getProduct(id).orElse(null);

        assertNotNull(updatedProduct);

        assertEquals(product.getName(), updatedProduct.getName());
        assertEquals(productUpdateRequest.description(), updatedProduct.getDescription());
        assertEquals(product.getPrice(), updatedProduct.getPrice());
    }

    @Test
    public void testUpdateProductWithOnlyPrice() {
        // Given
        Product product = new Product("Original Product",
                "Description for Original Product",
                59.99,
                "category for original");

        ProductUpdateRequest productUpdateRequest = new ProductUpdateRequest(
                "Updated Product",
                "Updated Description",
                69.99,
                "category");

        long id = 1;

        when(productDao.selectProductById(id)).thenReturn(Optional.of(product));

        // When
        productService.updateProduct(id, productUpdateRequest);

        // Then
        Product updatedProduct = productService.getProduct(id).orElse(null);

        assertNotNull(updatedProduct);

        assertEquals(product.getName(), updatedProduct.getName());
        assertEquals(product.getDescription(), updatedProduct.getDescription());
        assertEquals(productUpdateRequest.price(), updatedProduct.getPrice());
    }



    @Test
    public void testDeleteProduct() {
        // Given

        long id = 1;

        when(productDao.existsProductWithId(id)).thenReturn(true);

        // When
        productService.deleteProduct(id);

        // Then
        verify(productDao).deleteProduct(id);
    }

    @Test
    public void willThrowDeleteProductByIdNotExists() {
        // Given
        long productId = 1;

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

    @Test
    public void testGetProductsByCategory() {
        // Given
        String category = "Electronics";
        List<Product> expectedProducts = Arrays.asList(
                new Product("Original Product",
                        "Description for Original Product",
                        59.99,
                        "category for original"),
                new Product("Test Product 2",
                        "Description for Test Product 2",
                        29.99,
                        "category for original"
        ));

        when(productDao.getProductsByCategory(category)).thenReturn(expectedProducts);

        // When
        List<Product> result = productService.getProductsByCategory(category);

        // Then
        assertEquals(expectedProducts, result);
    }

    @Test
    public void testGetProductBySearch() {
        // Given
        String productName = "Laptop";
        List<Product> expectedProducts = Arrays.asList(
                new Product("Original Product",
                        "Description for Original Product",
                        59.99,
                        "category for original"),
                new Product("Test Product 2",
                        "Description for Test Product 2",
                        29.99,
                        "category for original"
                ));

        when(productDao.getProductsByName(productName)).thenReturn(expectedProducts);

        // When
        List<Product> result = productService.getProductBySearch(productName);

        // Then
        assertEquals(expectedProducts, result);
    }

    @Test
    public void testGetProductsSortedByPriceAsc() {
        // Given
        List<Product> products = Arrays.asList(
                new Product("Laptop", "Electronics", 1000.0, "category"),
                new Product("Smartphone", "Electronics", 500.0, "category"),
                new Product("Headphones", "Electronics", 200.0, "category")
        );
        List<Product> expectedSortedProducts = Arrays.asList(
                new Product("Headphones", "Electronics", 200.0, "category"),
                new Product("Smartphone", "Electronics", 500.0,  "category"),
                new Product("Laptop", "Electronics", 1000.0, "category")
        );
        when(productDao.getProductsSortedByPriceAsc(products)).thenReturn(expectedSortedProducts);

        // When
        List<Product> result = productService.getProductsSortedByPriceAsc(products);

        // Then
        assertEquals(expectedSortedProducts, result);
    }

    @Test
    public void testGetProductsSortedByPriceDesc() {
        // Given
        List<Product> products = Arrays.asList(
                new Product("Laptop", "Electronics", 1000.0, "category"),
                new Product("Smartphone", "Electronics", 500.0, "category"),
                new Product("Headphones", "Electronics", 200.0, "category")
        );
        List<Product> expectedSortedProducts = Arrays.asList(
                new Product("Laptop", "Electronics", 1000.0, "category"),
                new Product("Smartphone", "Electronics", 500.0, "category"),
                new Product("Headphones", "Electronics", 200.0, "category")
        );

        when(productDao.getProductsSortedByPriceDesc(products)).thenReturn(expectedSortedProducts);

        // When
        List<Product> result = productService.getProductsSortedByPriceDesc(products);

        // Then
        assertEquals(expectedSortedProducts, result);
    }

    @Test
    public void testGetAllCategories() {
        // Given
        List<String> expectedCategories = Arrays.asList("category1", "category2", "category3");
        when(productDao.getAllCategories()).thenReturn(expectedCategories);

        // When
        List<String> actualCategories = productService.getAllCategories();

        // Then
        assertEquals(expectedCategories, actualCategories);
    }

    @Test
    public void testGetProductForBasket() {
        // Given
        List<String> cartItems = Arrays.asList("1", "2", "3", "1", "2");
        List<Long> cartItemsLong = Arrays.asList(1L, 2L, 3L, 1L, 2L);

        Map<Long, Integer> productCountMap = new HashMap<>();
        for (long idProduct : cartItemsLong) {
            productCountMap.put(idProduct, productCountMap.getOrDefault(idProduct, 0) + 1);
        }

        List<Product> expectedProducts = Arrays.asList(
                new Product(1L, "Product1", "Description1", 10.0, "Category1", 2),
                new Product(2L, "Product2", "Description2", 20.0, "Category2", 2),
                new Product(3L, "Product3", "Description3", 30.0, "Category3", 1)
        );

        when(productDao.selectProductById(1L)).thenReturn(Optional.of(new Product(
                1L, "Product1", "Description1", 10.0, "Category1")));
        when(productDao.selectProductById(2L)).thenReturn(Optional.of(new Product(
                2L, "Product2", "Description2", 20.0, "Category2")));
        when(productDao.selectProductById(3L)).thenReturn(Optional.of(new Product(
                3L, "Product3", "Description3", 30.0, "Category3")));

        // When
        List<Product> actualProducts = productService.getProductForBasket(cartItems);

        // Then
        assertEquals(expectedProducts.size(), actualProducts.size());
        for (Product expectedProduct : expectedProducts) {
            assertEquals(expectedProduct, actualProducts.stream().filter(p -> p.getId().equals(expectedProduct.getId())).findFirst().orElse(null));
        }
    }

    @Test
    public void testGetTotalAmount() {
        // Given
        List<Product> productList = Arrays.asList(
                new Product(1L, "Product1", "Description1", 10.0, "Category1", 2),
                new Product(2L, "Product2", "Description2", 20.0, "Category2", 3),
                new Product(3L, "Product3", "Description3", 30.0, "Category3", 1)
        );

        // Expected total amount = (10.0 * 2) + (20.0 * 3) + (30.0 * 1) = 20.0 + 60.0 + 30.0 = 110.0

        // When
        double actualTotalAmount = productService.getTotalAmount(productList);

        // Then
        assertEquals(110.0, actualTotalAmount);
    }


    @Test
    public void testGetProductPriceDistribution() {
        // Given
        when(productDao.countProductsByPriceLessThan(50)).thenReturn(3);
        when(productDao.countProductsByPriceBetween(50, 100)).thenReturn(5);
        when(productDao.countProductsByPriceBetween(100, 150)).thenReturn(2);
        when(productDao.countProductsByPriceGreaterThanEqual(200)).thenReturn(1);

        // Expected distribution:
        // lessThan50: 3
        // between50And100: 5
        // between100And150: 2
        // over200: 1

        // When
        Map<String, Integer> actualPriceDistribution = productService.getProductPriceDistribution();

        // Then
        Map<String, Integer> expectedPriceDistribution = new HashMap<>();
        expectedPriceDistribution.put("lessThan50", 3);
        expectedPriceDistribution.put("between50And100", 5);
        expectedPriceDistribution.put("between100And150", 2);
        expectedPriceDistribution.put("over200", 1);

        assertEquals(expectedPriceDistribution, actualPriceDistribution);
    }


    @Test
    public void testGetProductCountByCategory() {
        // Given
        List<Object[]> mockResult = List.of(
                new Object[]{"Category1", 5L},
                new Object[]{"Category2", 8L}
        );

        when(productDao.getProductCountByCategory()).thenReturn(mockResult);

        // When
        Map<String, Long> actualProductCountByCategory = productService.getProductCountByCategory();

        // Then
        Map<String, Long> expectedProductCountByCategory = new HashMap<>();
        expectedProductCountByCategory.put("Category1", 5L);
        expectedProductCountByCategory.put("Category2", 8L);

        assertEquals(expectedProductCountByCategory, actualProductCountByCategory);
    }

    @Test
    public void testGetPage() {
        // Given
        List<Product> mockProducts = Arrays.asList(
                new Product(1L, "Product1", "Description1", 10.0, "Category1", 2),
                new Product(2L, "Product2", "Description2", 20.0, "Category2", 2),
                new Product(3L, "Product3", "Description3", 30.0, "Category3", 1)
        );
        int page = 1;
        int size = 2;

        // When
        List<Product> pageResult = productService.getPage(mockProducts, page, size);

        // Then
        assertEquals(1, pageResult.size());
        assertEquals("Product3", pageResult.get(0).getName());
    }

    @Test
    public void testGetTotalPages() {
        // Given
        int totalItems = 5;
        int size = 2;

        // When
        int totalPages = productService.getTotalPages(totalItems, size);

        // Then
        assertEquals(3, totalPages);
    }



}
