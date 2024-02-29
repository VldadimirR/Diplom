package ru.demo.shop.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.demo.shop.jwt.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthFilter, AuthenticationProvider authenticationProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Value("${spring.security.debug:false}")
    boolean securityDebug;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                .csrf().disable()
                .authorizeHttpRequests((authorize) ->
                        authorize
                                .requestMatchers(
                                        "api/admin/**")
                                .hasAuthority(
                                        "ROLE_ADMIN")
                                .requestMatchers(
                                        "/api/users/userPage"
                                )
                                .hasAuthority(
                                        "ROLE_USER"
                                )
                                .requestMatchers(
                                        "/auth/**",
                                        "/api/**",
                                        "/**" ).permitAll()
                                .anyRequest().permitAll()
                )
                .formLogin()
                .loginPage("/auth/login")
                .loginProcessingUrl("/process_login")
                .defaultSuccessUrl("/api/index")
                .failureUrl("/auth/login?error")
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout"))
                .logoutSuccessUrl("/auth/login")
                .and()
                .sessionManagement()
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.debug(securityDebug)
                .ignoring()
                .requestMatchers("/css/**", "/js/**", "/img/**", "/lib/**", "/favicon.ico", "/cart/**");
    }

}
