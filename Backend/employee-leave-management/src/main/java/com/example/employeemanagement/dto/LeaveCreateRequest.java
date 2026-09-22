package com.example.employeemanagement.dto;

import com.example.employeemanagement.model.LeaveType;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record LeaveCreateRequest(
        @NotNull LeaveType leaveType,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @NotBlank @Size(max=500) String reason
) {}
