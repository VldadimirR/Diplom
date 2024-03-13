package ru.demo.shop.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void testGenerateTokenOneArgument() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);

        when(userDetails.getUsername()).thenReturn("testUser");

        // Act
        String generatedToken = jwtService.generateToken(userDetails);

        // Assert
        assertNotNull(generatedToken);
        assertTrue(generatedToken.length() > 0);
    }

    @Test
    public void testGenerateToken() {
        // Arrange
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "USER");
        UserDetails userDetails = User.withUsername("testuser")
                .password("password")
                .roles("USER")
                .build();

        // Act
        String token = jwtService.generateToken(extraClaims, userDetails);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

}
