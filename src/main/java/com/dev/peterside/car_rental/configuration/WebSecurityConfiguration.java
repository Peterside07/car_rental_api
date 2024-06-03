package com.dev.peterside.car_rental.configuration;


import com.dev.peterside.car_rental.emuns.UserRole;
import com.dev.peterside.car_rental.repository.UserRepository;
import com.dev.peterside.car_rental.services.jwt.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity

public class WebSecurityConfiguration {

    private final JwtAuthenticationFilter JwtAuthenticationFilter;

    private final UserService userService;

    public WebSecurityConfiguration(com.dev.peterside.car_rental.configuration.JwtAuthenticationFilter jwtAuthenticationFilter, UserService userService) {
        JwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(
                        request -> request.requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/api/admin/**").hasAnyAuthority(UserRole.ADMIN.name()).
                                requestMatchers("/api/customer/**").hasAnyAuthority(UserRole.CUSTOMER.name()).
                                anyRequest().authenticated()).sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).
                authenticationProvider(authenticationProvider()).addFilterBefore(JwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);


        return http.build();

    }


    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userService.userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }
}
