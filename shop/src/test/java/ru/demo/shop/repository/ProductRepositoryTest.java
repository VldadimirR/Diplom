package ru.demo.shop.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.demo.shop.models.Product;
import ru.demo.shop.repositories.ProductRepository;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testExistsProductById() {
        // Given
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Description for Test Product");
        product.setPrice(29.99);

        productRepository.save(product);

        // When
        boolean exists = productRepository.existsProductById(product.getId());

        // Then
        assertTrue(exists);

        // When
        productRepository.deleteById(product.getId());

        // Then
        assertFalse(productRepository.existsProductById(product.getId()));
    }

    @Test
    public void existsProductByIdFailsWhenIdNotPresent() {
        // Given
        Long nonExistentProductId = 999L;

        // When
        boolean exists = productRepository.existsProductById(nonExistentProductId);

        // Then
        assertFalse(exists);
    }


}
