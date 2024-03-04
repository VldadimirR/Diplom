package ru.demo.shop.dao;

import jdk.jfr.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.demo.shop.models.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDao {

    List<Product> selectAllProducts();
    Optional<Product> selectProductById(Long productId);
    void insertProduct(Product product);
    void updateProduct(Product updatedProduct);
    void deleteProduct(Long productId);
    boolean existsProductWithId(Long productId);

    List<Product> getProductsSortedByPriceAsc(List<Product> products);
    List<Product> getProductsSortedByPriceDesc(List<Product> products);
    List<Product> getProductsByCategory(String category);

    List<Product> getProductsByName(String productName);

    List<String>getAllCategories();

    Integer countProductsByPriceLessThan(double i);
    Integer countProductsByPriceBetween(double i, double i1);
    Integer countProductsByPriceGreaterThanEqual(double i);
    List<Object[]> getProductCountByCategory();
}
