package ru.demo.shop.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.demo.shop.models.Role;
import ru.demo.shop.models.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsUserById(Long id);
    boolean existsByEmail(String email);
    long countByRole(Role role);
}
