package ru.demo.shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.demo.shop.models.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsProductById(Long id);

    List<Product> findByCategory(String category);

    List<Product> findByNameContainingIgnoreCase(String productName);

    @Query("SELECT DISTINCT p.category FROM Product p")
    List<String> getAllCategory();

    int countProductsByPriceLessThan(double price);

    int countProductsByPriceBetween(double minPrice, double maxPrice);

    int countProductsByPriceGreaterThanEqual(double price);

    @Query("SELECT p.category, COUNT(p) FROM Product p GROUP BY p.category")
    List<Object[]> getProductCountByCategory();
}
