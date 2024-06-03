package com.dev.peterside.car_rental.controller;

import com.dev.peterside.car_rental.dto.LoginRequest;
import com.dev.peterside.car_rental.dto.SignUpRequest;
import com.dev.peterside.car_rental.dto.UserDto;
import com.dev.peterside.car_rental.entitiy.User;
import com.dev.peterside.car_rental.repository.UserRepository;
import com.dev.peterside.car_rental.services.auth.AuthService;
import com.dev.peterside.car_rental.services.jwt.UserService;
import com.dev.peterside.car_rental.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    private final AuthService authService;

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private  final UserRepository userRepository;

    public AuthController(JwtUtil jwtUtil, AuthService authService, AuthenticationManager authenticationManager, UserService userService, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;

        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @RequestMapping("/signup")
    public ResponseEntity<?> signupCustomer(@RequestBody SignUpRequest signUpRequest) {
        if (authService.hasCustomerWithEmail(signUpRequest.getEmail()))
            return new ResponseEntity<>("Email already exists",
                    HttpStatus.BAD_REQUEST);
        UserDto createdCustomerDto = authService.createCustomer(signUpRequest);
        if (createdCustomerDto == null) return new ResponseEntity<>("User not created", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(createdCustomerDto, HttpStatus.CREATED);
    }

    @RequestMapping("/login")
    public LoginRequest loginCustomer(@RequestBody LoginRequest loginRequest) throws
            BadCredentialsException,
            DisabledException,
            UsernameNotFoundException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect username or password", e);
        } catch (DisabledException e) {
            throw new DisabledException("User is disabled", e);
        }
        final UserDetails userDetails = userService.userDetailsService().loadUserByUsername(loginRequest.getEmail());
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());
        final String token = jwtUtil.generateToken(userDetails);
        LoginRequest loginResponse = new LoginRequest();

        if (optionalUser.isPresent()) {
            loginRequest.setJwt(token);
            loginRequest.setUserId(optionalUser.get().getId());
            loginRequest.setRole(optionalUser.get().getEmail());

        }
        return loginResponse;

    }
    }