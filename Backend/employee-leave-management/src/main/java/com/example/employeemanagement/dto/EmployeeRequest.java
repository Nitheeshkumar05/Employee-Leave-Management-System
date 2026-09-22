package com.example.employeemanagement.dto;

import com.example.employeemanagement.model.EmployeeRole;
import jakarta.validation.constraints.*;

public record EmployeeRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String phone,
        @Size(min=6, message="Password must contain at least 6 characters") String password,
        EmployeeRole role,
        Long departmentId
) {}
