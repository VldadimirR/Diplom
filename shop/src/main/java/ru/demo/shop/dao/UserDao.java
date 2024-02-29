package ru.demo.shop.dao;

import ru.demo.shop.models.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    List<User> selectAllUsers();
    Optional<User> selectUserById(Long userId);
    void insertUser(User user);
    void updateUser(User updatedUser);
    void deleteUser(Long userId);
    boolean existsUserWithId(Long userId);
    long getUserIdByUsername(String username);
    boolean isEmailAlreadyInUse(String email);
}
