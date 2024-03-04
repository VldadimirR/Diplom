package ru.demo.shop.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.demo.shop.exception.ProductNotFoundException;
import ru.demo.shop.models.Product;
import ru.demo.shop.models.Role;
import ru.demo.shop.models.User;
import ru.demo.shop.services.ProductService;
import ru.demo.shop.services.UserService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    private final UserService userService;

    @Autowired
    public ProductController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping("/products")
    public String getAllProducts(
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String productName,
            Model model) {

        List<Product> products;
        List<String> categories = productService.getAllCategories();


        if (productName != null && !productName.isEmpty()) {
            products = productService.getProductBySearch(productName);
        } else {
            if (category != null && !category.equals("all")) {
                products = productService.getProductsByCategory(category);
            } else {
                products = productService.getAllProducts();
            }

            if ("priceLowToHigh".equals(sort)) {
                products = productService.getProductsSortedByPriceAsc(products);
            } else if ("priceHighToLow".equals(sort)) {
                products = productService.getProductsSortedByPriceDesc(products);
            }
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);

        try {
            Optional<User> userOptional = userService.getCurrentUser();
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                model.addAttribute("user", user);
                Role role = userService.getCurrentUserRole();
                model.addAttribute("role", role != null ? role.toString() : "ROLE_ANONYMOUS");
            } else {
                model.addAttribute("role", "ROLE_ANONYMOUS");
                return "catalog";
            }
        } catch (Exception e){
        }
        return "catalog";
    }


    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Long productId) {
        return productService.getProduct(productId)
                .map(product -> new ResponseEntity<>(product, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        productService.addProduct(product);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId, @RequestBody Product updatedProduct) {
        try {
            productService.updateProduct(productId, updatedProduct);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ProductNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        try {
            productService.deleteProduct(productId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ProductNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/price")
    public ResponseEntity<?> getProductPrices() {
        try {
            Map<String, Integer> priceDistribution = productService.getProductPriceDistribution();
            return ResponseEntity.ok(priceDistribution);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching product prices");
        }
    }

    @GetMapping("/count-by-category")
    public ResponseEntity<?>  getProductCountByCategory() {
        Map<String, Long> productCountByCategory = productService.getProductCountByCategory();
        return ResponseEntity.ok(productCountByCategory);
    }

}
