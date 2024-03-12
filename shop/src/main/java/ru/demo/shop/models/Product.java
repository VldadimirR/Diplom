package ru.demo.shop.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String name;
    @NonNull
    private String description;
    @NonNull
    private Double price;
    @NonNull
    private String category;

    @Transient
    private Integer quantity;

    public Product(String name, String description, Double price, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    public Product(long l, String product1, String description1, double v, String category1, int i) {
        this.id = l;
        this.name = product1;
        this.description = description1;
        this.price = v;
        this.category = category1;
        this.quantity = i;
    }

    public Product(long l, String product1, String description1, double v, String category1) {
        this.id = l;
        this.name = product1;
        this.description = description1;
        this.price = v;
        this.category = category1;
    }

}
