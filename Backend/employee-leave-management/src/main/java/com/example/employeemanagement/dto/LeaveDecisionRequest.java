package com.example.employeemanagement.dto;

import jakarta.validation.constraints.Size;
public record LeaveDecisionRequest(@Size(max=500, message="HR note must be 500 characters or less") String note) {}
