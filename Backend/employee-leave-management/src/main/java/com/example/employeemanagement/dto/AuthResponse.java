package com.example.employeemanagement.dto;

public record AuthResponse(Long id, String name, String email, String phone, String role, String department, String token) {}
