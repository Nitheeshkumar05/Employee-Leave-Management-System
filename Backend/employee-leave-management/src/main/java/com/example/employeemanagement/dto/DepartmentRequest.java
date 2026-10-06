package com.example.employeemanagement.dto;
import jakarta.validation.constraints.NotBlank;
public record DepartmentRequest(@NotBlank String name) {}
