package ru.demo.shop.auth;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.demo.shop.jwt.JwtService;
import ru.demo.shop.models.User;
import ru.demo.shop.repositories.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    public void testRegister() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setFirstname("John");
        request.setPhone("4234234234");
        request.setAddress("new address");
        request.setEmail("john@example.com");
        request.setPassword("password");

        User user = mock(User.class);

        String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG" +
                "9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(jwtService.generateToken(any(User.class))).thenReturn(jwtToken);

        // When
        AuthenticationResponse response = authenticationService.register(request);

        // Then
        verify(userRepository, times(1)).save(any(User.class));

        verify(jwtService, times(1)).generateToken(any(User.class));

        assertNotNull(response);
        assertEquals(jwtToken, response.getToken());
    }
}
