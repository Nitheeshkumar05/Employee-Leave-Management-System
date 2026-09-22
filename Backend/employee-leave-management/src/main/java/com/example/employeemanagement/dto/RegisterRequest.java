package com.example.employeemanagement.dto;

import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank(message="Name is required") String name,
        @NotBlank @Email(message="Enter a valid email") String email,
        @NotBlank(message="Phone number is required") String phone,
        @NotBlank @Size(min=6, message="Password must contain at least 6 characters") String password
) {}
