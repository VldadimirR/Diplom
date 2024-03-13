package ru.demo.shop.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import ru.demo.shop.models.Role;
import ru.demo.shop.models.User;

import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class JwtAuthenticationFilterTest {

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    public void testFilterWithValidJwtToken() throws Exception {
        // Arrange
        String jwt = "valid_jwt_token";
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);

        when(jwtService.extractUsername(jwt)).thenReturn("user@example.com");

        UserDetails userDetails = new User(
                1L,
                "name",
                "user@example.com",
                "89004903423",
                "NewAddres",
                "password",
                Role.ROLE_USER);

        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

        when(jwtService.isTokenValid(jwt, userDetails)).thenReturn(true);

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtService, times(1)).extractUsername(jwt);
        verify(filterChain, times(1)).doFilter(request, response);
        verify(userDetailsService, times(1)).loadUserByUsername("user@example.com");
        verify(jwtService, times(1)).isTokenValid(jwt, userDetails);
        verifyNoMoreInteractions(jwtService, userDetailsService, filterChain);
    }

    @Test
    public void testFilterWithoutJwtToken() throws Exception {
        // Arrange
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verifyNoMoreInteractions(filterChain);
    }
}
