package ru.demo.shop.dataAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.demo.shop.models.User;
import ru.demo.shop.repositories.UserRepository;
import ru.demo.shop.services.UserJPADataAccessService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserJPADataAccessServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserJPADataAccessService userService;

    @Test
    void selectAllUsers() {
        // Arrange
        List<User> users = Arrays.asList(new User(), new User());
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> result = userService.selectAllUsers();

        // Assert
        assertEquals(users, result);
    }

    @Test
    void selectUserById() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.selectUserById(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void insertUser() {
        // Arrange
        User user = new User();

        // Act
        userService.insertUser(user);

        // Assert
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUser() {
        // Arrange
        User updatedUser = new User();

        // Act
        userService.updateUser(updatedUser);

        // Assert
        verify(userRepository, times(1)).save(updatedUser);
    }

    @Test
    void deleteUser() {
        // Arrange
        Long userId = 1L;

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void existsUserWithId() {
        // Arrange
        Long userId = 1L;
        when(userRepository.existsUserById(userId)).thenReturn(true);

        // Act
        boolean result = userService.existsUserWithId(userId);

        // Assert
        assertTrue(result);
    }
}

