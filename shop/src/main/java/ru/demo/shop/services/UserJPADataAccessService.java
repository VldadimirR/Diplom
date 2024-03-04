package ru.demo.shop.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.demo.shop.dao.UserDao;
import ru.demo.shop.models.Role;
import ru.demo.shop.models.User;
import ru.demo.shop.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Repository("jpaUser")
public class UserJPADataAccessService implements UserDao {

    private final UserRepository userRepository;

    @Autowired
    public UserJPADataAccessService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> selectAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> selectUserById(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public void insertUser(User user) {
         userRepository.save(user);
    }

    @Override
    public void updateUser(User updatedUser) {
         userRepository.save(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public boolean existsUserWithId(Long userId) {
        return userRepository.existsUserById(userId);
    }

    @Override
    public long getUserIdByUsername(String username) {
        Optional<User> userOptional = userRepository.findByEmail(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user.getId();
        } else {
            return 0L;
        }
    }

    @Override
    public boolean isEmailAlreadyInUse(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public long countByRole(Role role) {
        return userRepository.countByRole(role);
    }
}