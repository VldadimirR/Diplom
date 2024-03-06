package ru.demo.shop.request;

public record ProductUpdateRequest(
        String name,
        String description,
        Double price,
        String category
) {
}
