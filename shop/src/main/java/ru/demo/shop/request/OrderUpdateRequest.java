package ru.demo.shop.request;

import ru.demo.shop.models.Status;

public record OrderUpdateRequest(
        Status status
) {
}
