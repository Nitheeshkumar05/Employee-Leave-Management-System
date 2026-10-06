package com.example.employeemanagement.dto;

import com.example.employeemanagement.model.*;
import java.time.*;

public record LeaveResponse(
        Long id, Long employeeId, String employeeName, String employeeEmail, String department,
        LeaveType leaveType, LocalDate startDate, LocalDate endDate, long days,
        String reason, LeaveStatus status, String hrNote, String reviewedBy,
        LocalDateTime requestedAt, LocalDateTime reviewedAt
) {}
