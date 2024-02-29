package ru.demo.shop.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

}
