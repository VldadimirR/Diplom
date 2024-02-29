package ru.demo.shop.sevices;


import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.demo.shop.request.UserUpdateRequest;
import ru.demo.shop.dao.UserDao;
import ru.demo.shop.exception.UserNotFoundException;
import ru.demo.shop.models.User;
import ru.demo.shop.repositories.UserRepository;
import ru.demo.shop.services.UserService;

import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserServiceTest {

    @Mock
    private UserDao userDao;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;


    @Test
    public void testGetAllUsers() {
        // Given
        User user1 = new User("JohnDoe", "John Doe");
        User user2 = new User("JaneSmith", "Jane Smith");

        userRepository.saveAll(List.of(user1,user2));

        // When
        List<User> users = userService.getAllUsers();

        // Then
        assertNotNull(users);
        TestCase.assertEquals(2, users.size());
    }

    @Test
    public void testGetUserById(){
        // Given
        User user1 = new User("JohnDoe", "John Doe");
        User saveUser = userRepository.save(user1);

        // When
        Optional<User> retrievedUser = userService.getUser(saveUser.getId());

        // Then
        assertTrue(retrievedUser.isPresent());
        assertEquals(saveUser.getId(), retrievedUser.get().getId());

    }

    @Test
    public void testGetUserThrowsExceptionWhenNotFound(){
        // Given
        Long userId = 1L;
        when(userDao.selectUserById(userId)).thenReturn(Optional.empty());

        // When, Then
        assertThatThrownBy(() ->
                userService.getUser(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("user with id [%s] not found".formatted(userId));
    }

    @Test
    public void testAddUser() {
        // Given
        User user = new User("NewUser", "New User");

        // When
        userService.addUser(user);

        // Then
        User addedUser = userService.getUser(user.getId()).orElse(null);

        assertNotNull(addedUser);
        assertEquals("NewUser", addedUser.getUsername());
        assertEquals("New User", addedUser.getFullName());
    }

    @Test
    public void testUpdateUser(){
        // Given
        User user = new User("NewUser", "New User");
        User saveUser = userRepository.save(user);

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("UpdateUser", "Update User");

        // When

        userService.updateUser(saveUser.getId(),userUpdateRequest);

        // Then
        User updateUser = userService.getUser(user.getId()).orElse(null);
        assertNotNull(updateUser);
        assertEquals(userUpdateRequest.fullName(), updateUser.getFullName());
        assertEquals(userUpdateRequest.username(), updateUser.getUsername());
    }

    @Test
    public void testUpdateUserWithOnlyUsername(){
        // Given
        User user = new User("NewUser", "New User");
        User saveUser = userRepository.save(user);

        // When
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("UpdateUser", "Update User");
        userService.updateUser(saveUser.getId(),userUpdateRequest);

        // Then
        User updateUser = userService.getUser(user.getId()).orElse(null);
        assertNotNull(updateUser);

        // Проверяем, что обновлено только поле "Username"
        assertEquals(user.getFullName(), updateUser.getFullName());
        assertEquals(userUpdateRequest.username(), updateUser.getUsername());
    }


    @Test
    public void testUpdateUserWithOnlyFullName(){
        // Given
        User user = new User("NewUser", "New User");
        User saveUser = userRepository.save(user);

        // When
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("UpdateUser", "Update User");
        userService.updateUser(saveUser.getId(),userUpdateRequest);

        // Then
        User updateUser = userService.getUser(user.getId()).orElse(null);
        assertNotNull(updateUser);

        // Проверяем, что обновлено только поле "FullName"
        assertEquals(userUpdateRequest.fullName(), updateUser.getFullName());
        assertEquals(user.getUsername(), updateUser.getUsername());
    }



    @Test
    public void testDeleteUser() {
        // Given
        User user = new User("NewUser", "New User");
        User savedUser = userRepository.save(user);

        // When
        userService.deleteUser(savedUser.getId());

        // Then
        Optional<User> deleteUser = userRepository.findById(savedUser.getId());
        assertFalse(deleteUser.isPresent());
    }

    @Test
    public void willThrowDeleteUserByIdNotExists() {
        // Given
        Long userId = 1L;
        when(userDao.existsUserWithId(userId)).thenReturn(false);

        // When
        assertThatThrownBy(() ->
                userService.deleteUser(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("user with id [%s] not found"
                        .formatted(userId));
        // Then
        verify(userDao, never()).deleteUser(userId);
    }

}
