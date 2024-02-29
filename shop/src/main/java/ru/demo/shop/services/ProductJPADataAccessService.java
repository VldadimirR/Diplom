package ru.demo.shop.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.demo.shop.dao.ProductDao;
import ru.demo.shop.models.Product;
import ru.demo.shop.repositories.ProductRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository("jpaProduct")
public class ProductJPADataAccessService implements ProductDao {


    private final ProductRepository productRepository;

    @Autowired
    public ProductJPADataAccessService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> selectAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> selectProductById(Long productId) {
        return productRepository.findById(productId);
    }

    @Override
    public void insertProduct(Product product) {
         productRepository.save(product);
    }

    @Override
    public void updateProduct(Product updatedProduct) {
         productRepository.save(updatedProduct);
    }

    @Override
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    @Override
    public boolean existsProductWithId(Long productId) {
        return productRepository.existsProductById(productId);
    }

    @Override
    public List<Product> getProductsSortedByPriceAsc(List<Product> products) {
        return products.stream()
                .sorted(Comparator.comparingDouble(Product::getPrice))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> getProductsSortedByPriceDesc(List<Product> products) {
        return products.stream()
                .sorted(Comparator.comparingDouble(Product::getPrice).reversed())
                .collect(Collectors.toList());
    }
    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> getProductsByName(String productName) {
        return productRepository.findByNameContainingIgnoreCase(productName);
    }

    @Override
    public List<String> getAllCategories() {
        return productRepository.getAllCategory();
    }


}
