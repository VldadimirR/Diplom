package ru.demo.shop.sevices;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.demo.shop.dao.UserDao;
import ru.demo.shop.exception.UserNotFoundException;
import ru.demo.shop.models.Role;
import ru.demo.shop.models.User;
import ru.demo.shop.request.UserUpdateRequest;
import ru.demo.shop.services.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @InjectMocks
    private UserService userService;

    @Test
    public void testGetAllUsers() {
        // Given
        User user1 = new User("User1",
                "user1@example.com",
                "123456789",
                "Address1",
                "password",
                Role.ROLE_USER);

        User user2 = new User(
                "User2",
                "user2@example.com",
                "987654321",
                "Address2",
                "password",
                Role.ROLE_USER);

        when(userDao.selectAllUsers()).thenReturn(List.of(user1,user2));

        // When
        List<User> users = userService.getAllUsers();


        // Then
        assertNotNull(users);
        TestCase.assertEquals(2, users.size());
    }

    @Test
    public void testGetUserById(){
        // Given
        User user1 = new User("User1",
                "user1@example.com",
                "123456789",
                "Address1",
                "password",
                Role.ROLE_USER);

        long id = 1;

        when(userDao.selectUserById(id)).thenReturn(Optional.of(user1));

        // When
        Optional<User> retrievedUser = userService.getUser(id);

        // Then
        assertTrue(retrievedUser.isPresent());

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
    public void testUpdateUser(){
        // Given
        User user = new User("User1",
                "user1@example.com",
                "123456789",
                "Address1",
                "password",
                Role.ROLE_USER);

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                "UpdateUser",
                "userUpdate@example.com",
                "345345345",
                "new Address",
                Role.ROLE_USER
                );

        long id = 1;

        when(userDao.selectUserById(id)).thenReturn(Optional.of(user));

        // When
        userService.updateUser(id,userUpdateRequest);

        // Then
        User updateUser = userService.getUser(id).orElse(null);

        assertNotNull(updateUser);
        assertEquals(userUpdateRequest.firstname(), updateUser.getFirstname());
        assertEquals(userUpdateRequest.email(), updateUser.getEmail());
    }

    @Test
    public void testUpdateUserWithOnlyFirstname(){
        // Given
        User user = new User("User1",
                "user1@example.com",
                "123456789",
                "Address1",
                "password",
                Role.ROLE_USER);

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                "UpdateUser",
                "user1@example.com",
                "345345345",
                "new Address",
                Role.ROLE_USER
        );

        long id = 1;

        when(userDao.selectUserById(id)).thenReturn(Optional.of(user));

        // When
        userService.updateUser(id,userUpdateRequest);

        // Then
        User updateUser = userService.getUser(id).orElse(null);

        assertNotNull(updateUser);
        assertEquals(user.getFirstname(), updateUser.getFirstname());
        assertEquals(userUpdateRequest.email(), updateUser.getEmail());
    }


    @Test
    public void testUpdateUserWithOnlyEmail(){
        // Given
        User user = new User("User1",
                "user1@example.com",
                "123456789",
                "Address1",
                "password",
                Role.ROLE_USER);


        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                "UpdateUser",
                "user1@example.com",
                "345345345",
                "new Address",
                Role.ROLE_USER
        );
        long id = 1;

        when(userDao.selectUserById(id)).thenReturn(Optional.of(user));

        // When
        userService.updateUser(id,userUpdateRequest);

        // Then
        User updateUser = userService.getUser(id).orElse(null);

        assertNotNull(updateUser);
        assertEquals(userUpdateRequest.firstname(), updateUser.getFirstname());
        assertEquals(user.getUsername(), updateUser.getUsername());
    }



    @Test
    public void testDeleteUser() {

        long id = 10;

        when(userDao.existsUserWithId(id)).thenReturn(true);

        // When
        userService.deleteUser(id);

        // Then
        verify(userDao).deleteUser(id);
    }

    @Test
    public void testGetUserIdByUsername() {
        // Given
        String username = "testUser";
        Long expectedUserId = 123L;

        when(userDao.getUserIdByUsername(username)).thenReturn(expectedUserId);

        // When
        Long actualUserId = userService.getUserIdByUsername(username);

        // Then
        assertEquals(expectedUserId, actualUserId);
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

    @Test
    public void testIsEmailAlreadyInUse_EmailNotInUse() {
        // Given
        String email = "test@example.com";
        when(userDao.isEmailAlreadyInUse(email)).thenReturn(false);

        // When
        boolean result = userService.isEmailAlreadyInUse(email);

        // Then
        assertFalse(result);
    }

    @Test
    public void testIsEmailAlreadyInUse_EmailAlreadyInUse() {
        // Given
        String email = "test@example.com";
        when(userDao.isEmailAlreadyInUse(email)).thenReturn(true);

        // When
        boolean result = userService.isEmailAlreadyInUse(email);

        // Then
        assertTrue(result);
    }

    @Test
    public void testGetUsersByRole() {
        // Given
        long adminCount = 5L;
        long userCount = 10L;

        when(userDao.countByRole(Role.ROLE_ADMIN)).thenReturn(adminCount);
        when(userDao.countByRole(Role.ROLE_USER)).thenReturn(userCount);

        // When
        Map<String, Integer> result = userService.getUsersByRole();

        // Then
        Map<String, Integer> expectedResult = new HashMap<>();
        expectedResult.put("ROLE_ADMIN", (int) adminCount);
        expectedResult.put("ROLE_USER", (int) userCount);

        assertEquals(expectedResult, result);
    }


//    @Test
//    public void testGetCurrentUser() {
//        // Given
//        String username = "testUser";
//        Long userId = 123L;
//        User expectedUser = new User(userId,
//                "User1",
//                "user1@example.com",
//                "123456789",
//                "Address1",
//                "password",
//                Role.ROLE_USER);
//
//
//        // Подменяем контекст аутентификации
//        Authentication authentication = new UsernamePasswordAuthenticationToken("user1@example.com", "password");
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        // Задаем поведение мок-объекта
//        when(userDao.getUserIdByUsername(username)).thenReturn(userId);
//        when(userDao.selectUserById(userId)).thenReturn(Optional.of(expectedUser));
//
//
//        // When
//        Optional<User> actualUserOptional = userService.getCurrentUser();
//        System.out.println(actualUserOptional);
//
//        // Then
//        assertEquals(expectedUser, actualUserOptional.orElse(null));
//    }


}
