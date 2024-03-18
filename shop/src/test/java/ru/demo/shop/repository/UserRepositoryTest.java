package ru.demo.shop.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import ru.demo.shop.models.Role;
import ru.demo.shop.models.User;
import ru.demo.shop.repositories.UserRepository;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testExistsUserById() {
        User user = new User("User1",
                "user1@example.com",
                "123456789",
                "Address1",
                "password",
                Role.ROLE_USER);
        User saveUser = userRepository.save(user);

        // When
        boolean exists = userRepository.existsUserById(user.getId());

        // Then
        assertTrue(exists);

        // When
        userRepository.deleteById(user.getId());

        // Then
        assertFalse(userRepository.existsUserById(user.getId()));
    }

    @Test
    public void existsUserByIdFailsWhenIdNotPresent() {
        // Given
        Long nonExistentUserId = 999L;

        // When
        boolean exists = userRepository.existsUserById(nonExistentUserId);

        // Then
        assertFalse(exists);
    }
}

