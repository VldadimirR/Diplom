package ru.demo.shop.request;

import ru.demo.shop.models.Role;

public record UserUpdateRequest(
        String firstname,
        String email,
        String phone,
        String address,
        Role role
) {
}
