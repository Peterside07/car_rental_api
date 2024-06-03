package com.dev.peterside.car_rental.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setJwt(String token) {
    }

    public void setUserId(Long id) {
    }

    public void setRole(String email) {
    }
}
