package com.dev.peterside.car_rental.services.auth;

import com.dev.peterside.car_rental.dto.SignUpRequest;
import com.dev.peterside.car_rental.dto.UserDto;

public interface AuthService {

    UserDto createCustomer(SignUpRequest signUpRequest);

    boolean hasCustomerWithEmail (String email);
}
