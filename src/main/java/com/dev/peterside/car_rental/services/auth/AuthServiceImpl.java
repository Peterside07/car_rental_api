package com.dev.peterside.car_rental.services.auth;

import com.dev.peterside.car_rental.dto.SignUpRequest;
import com.dev.peterside.car_rental.dto.UserDto;
import com.dev.peterside.car_rental.emuns.UserRole;
import com.dev.peterside.car_rental.entitiy.User;
import com.dev.peterside.car_rental.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createCustomer(SignUpRequest signUpRequest) {
        User user = new User();
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(new BCryptPasswordEncoder().encode(signUpRequest.getPassword()));
        user.setUserRole(UserRole.CUSTOMER);
        User savedUser = userRepository.save(user);
        UserDto userDto = new UserDto();
        userDto.setId(savedUser.getId());
        userDto.setFirstName(savedUser.getFirstName());
        userDto.setLastName(savedUser.getLastName());
        userDto.setEmail(savedUser.getEmail());
        userDto.setUserRole(savedUser.getUserRole().name());
        return userDto;

    }

    @Override
    public boolean hasCustomerWithEmail(String email) {
         return userRepository.findByEmail(email).isPresent();
    }
}
