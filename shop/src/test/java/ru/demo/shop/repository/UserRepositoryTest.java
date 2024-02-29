package ru.demo.shop.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.demo.shop.models.User;
import ru.demo.shop.repositories.UserRepository;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testExistsUserById() {
        // Given
        User user = new User();
        user.setUsername("TestUser");
        user.setFullName("test user");

        userRepository.save(user);

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

