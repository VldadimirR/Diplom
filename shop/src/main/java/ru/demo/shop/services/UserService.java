package ru.demo.shop.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import ru.demo.shop.models.Role;
import ru.demo.shop.request.UserUpdateRequest;
import ru.demo.shop.dao.UserDao;
import ru.demo.shop.exception.RequestValidationException;
import ru.demo.shop.exception.UserNotFoundException;
import ru.demo.shop.models.User;


import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> getAllUsers() {
        return userDao.selectAllUsers();
    }

    public Optional<User> getUser(Long userId) {
        return  Optional.ofNullable(userDao.selectUserById(userId)
                .orElseThrow( () -> new UserNotFoundException(
                        "user with id [%s] not found".formatted(userId)
                )));
    }

    public void addUser(User user) {
        // Возможно, здесь вам также нужно выполнить дополнительные проверки или преобразования перед сохранением пользователя.
         userDao.insertUser(user);
    }

    public void updateUser(Long userId, UserUpdateRequest updateRequest) {
        // Получаем существующего пользователя по его идентификатору
        User existingUser = userDao.selectUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "User with id " + userId + " not found"
                ));

        boolean changes = false;

        // Проверяем, были ли внесены изменения в данные пользователя
        if (updateRequest.firstname() != null && !updateRequest.firstname().equals(existingUser.getFirstname())) {
            existingUser.setFirstname(updateRequest.firstname());
            changes = true;
        }

        if (updateRequest.email() != null && !updateRequest.email().equals(existingUser.getEmail())) {
            existingUser.setEmail(updateRequest.email());
            changes = true;
        }

        if (updateRequest.phone() != null && !updateRequest.phone().equals(existingUser.getPhone())) {
            existingUser.setPhone(updateRequest.phone());
            changes = true;
        }

        if (updateRequest.address() != null && !updateRequest.address().equals(existingUser.getAddress())) {
            existingUser.setAddress(updateRequest.address());
            changes = true;
        }

        if (updateRequest.role() != null && !updateRequest.role().equals(existingUser.getRole())) {
            existingUser.setRole(updateRequest.role());
            changes = true;
        }

        // Если изменений не было, выбрасываем исключение с сообщением
        if (!changes) {
            throw new RequestValidationException("No data changes found");
        }

        // Обновляем пользователя в базе данных
        userDao.updateUser(existingUser);
    }



    public void deleteUser(Long userId) {
        if(!userDao.existsUserWithId(userId)){
            throw new UserNotFoundException(
                    "user with id [%s] not found".formatted(userId)
            );
        }
        userDao.deleteUser(userId);
    }

    public Long getUserIdByUsername(String username) {
        return userDao.getUserIdByUsername(username);
    }


    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Long id = getUserIdByUsername(username);
            return getUser(id);
        }
        return Optional.empty();
    }

    public Role getCurrentUserRole() {
        Optional<User> currentUserOptional = getCurrentUser();
        return currentUserOptional.map(User::getRole).orElse(null);
    }

    public boolean isEmailAlreadyInUse(String email) {
        return userDao.isEmailAlreadyInUse(email);
    }
}
