package ru.demo.shop.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import ru.demo.shop.dao.UserDao;
import ru.demo.shop.exception.RequestValidationException;
import ru.demo.shop.exception.UserNotFoundException;
import ru.demo.shop.models.Role;
import ru.demo.shop.models.User;
import ru.demo.shop.request.UserUpdateRequest;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public void updateUser(Long userId, UserUpdateRequest updateRequest) {
        User existingUser = userDao.selectUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "User with id " + userId + " not found"
                ));

        boolean changes = false;

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

        if (!changes) {
            throw new RequestValidationException("No data changes found");
        }

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


    public boolean isEmailAlreadyInUse(String email) {
        return userDao.isEmailAlreadyInUse(email);
    }

    public Map<String, Integer> getUsersByRole() {
        Map<String, Integer> usersByRole = new HashMap<>();

        long adminCount = userDao.countByRole(Role.ROLE_ADMIN);
        long userCount = userDao.countByRole(Role.ROLE_USER);

        usersByRole.put("ROLE_ADMIN", (int) adminCount);
        usersByRole.put("ROLE_USER", (int) userCount);

        return usersByRole;
    }

    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Long id = getUserIdByUsername(username);
            return userDao.selectUserById(id);
        }
        return Optional.empty();
    }

    public Role getCurrentUserRole() {
        Optional<User> currentUserOptional = getCurrentUser();
        return currentUserOptional.map(User::getRole).orElse(null);
    }

    public void setUserAndRoleAttributes(Model model, Principal principal) {
        if (principal != null) {
            try {
                Optional<User> userOptional = getCurrentUser();
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    model.addAttribute("user", user);
                    Role role = getCurrentUserRole();
                    model.addAttribute("role", role != null ? role.toString() : "ROLE_ANONYMOUS");
                }
            } catch (Exception e) {

            }
        } else {
            model.addAttribute("role", "ROLE_ANONYMOUS");
        }
    }
}
