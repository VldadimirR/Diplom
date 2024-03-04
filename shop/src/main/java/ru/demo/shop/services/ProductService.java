package ru.demo.shop.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.demo.shop.dao.ProductDao;
import ru.demo.shop.exception.ProductNotFoundException;
import ru.demo.shop.models.Product;

import java.util.*;

@Service
public class ProductService {

    private final ProductDao productDao;

    @Autowired
    public ProductService(@Qualifier("jpaProduct") ProductDao productDao) {
        this.productDao = productDao;

    }

    public List<Product> getAllProducts() {
        return productDao.selectAllProducts();
    }

    public Optional<Product> getProduct(Long productId) {
        return Optional.ofNullable(productDao.selectProductById(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        "product with id [%s] not found".formatted(productId)
                )));
    }

    public void addProduct(Product product) {
         productDao.insertProduct(product);
    }

    public void updateProduct(Long productId, Product updatedProduct) {
        Optional<Product> existingProductOptional = productDao.selectProductById(productId);
        if (existingProductOptional.isPresent()) {
            Product existingProduct = existingProductOptional.get();

            if (updatedProduct.getName() != null) {
                existingProduct.setName(updatedProduct.getName());
            } else if (updatedProduct.getDescription() != null) {
                existingProduct.setDescription(updatedProduct.getDescription());
            } else if (updatedProduct.getPrice() != null) {
                existingProduct.setPrice(updatedProduct.getPrice());
            }

            productDao.updateProduct(existingProduct);
        } else {
            throw new ProductNotFoundException("Product with id [%s] not found".formatted(productId));
        }
    }

    public void deleteProduct(Long productId) {
        if(!productDao.existsProductWithId(productId)){
            throw new ProductNotFoundException(
                "product with id [%s] not found".formatted(productId)
            );
        }
        productDao.deleteProduct(productId);
    }

    public  List<Product> getProductsByCategory(String category) {
        return productDao.getProductsByCategory(category);
    }

    public List<Product> getProductBySearch(String productName){
        return  productDao.getProductsByName(productName);
    }


    public List<Product> getProductsSortedByPriceAsc(List<Product> product){
        return productDao.getProductsSortedByPriceAsc(product);
    }
    public List<Product> getProductsSortedByPriceDesc(List<Product> product){
        return productDao.getProductsSortedByPriceDesc(product);
    }

    public List<String> getAllCategories() {
        return productDao.getAllCategories();
    }

    public List<Product> getProductForBasket(List<String> cartItems) {
        List<Long> cartItemsLong = cartItems.stream()
                .map(Long::valueOf)
                .toList();

        Map<Long, Integer> productCountMap = new HashMap<>();
        for (long idProduct : cartItemsLong) {
            productCountMap.put(idProduct, productCountMap.getOrDefault(idProduct, 0) + 1);
        }

        List<Product> productList = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : productCountMap.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            Optional<Product> productOptional = productDao.selectProductById(productId);
            productOptional.ifPresent(product -> {
                product.setQuantity(quantity);
                productList.add(product);
            });
        }

        return productList;
    }

    public double calculateTotalCost(Long productId, int quantity) {
        Product product = productDao.selectProductById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found with ID: " + productId));

        return product.getPrice() * quantity;
    }

    public double getTotalAmount(List<Product> productList) {
        double totalAmount = 0.0;

        for (Product product : productList) {
            double productPrice = product.getPrice();
            int quantity = product.getQuantity();
            totalAmount += productPrice * quantity;
        }
        // Округлить до двух знаков после запятой
        totalAmount = Math.round(totalAmount * 100.0) / 100.0;

        return totalAmount;
    }

    public Map<String, Integer> getProductPriceDistribution() {
        Map<String, Integer> priceDistribution = new HashMap<>();
        priceDistribution.put("lessThan50", productDao.countProductsByPriceLessThan(50));
        priceDistribution.put("between50And100", productDao.countProductsByPriceBetween(50, 100));
        priceDistribution.put("between100And150", productDao.countProductsByPriceBetween(100, 150));
        priceDistribution.put("over200", productDao.countProductsByPriceGreaterThanEqual(200));
        return priceDistribution;
    }

    public Map<String, Long> getProductCountByCategory() {
        List<Object[]> result = productDao.getProductCountByCategory();

        Map<String, Long> productCountByCategory = new HashMap<>();
        for (Object[] row : result) {
            String category = (String) row[0];
            Long count = (Long) row[1];
            productCountByCategory.put(category, count);
        }
        return productCountByCategory;
    }
}
