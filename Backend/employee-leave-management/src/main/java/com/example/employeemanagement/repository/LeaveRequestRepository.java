package com.example.employeemanagement.repository;

import com.example.employeemanagement.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployeeIdOrderByRequestedAtDesc(Long employeeId);
    List<LeaveRequest> findAllByOrderByRequestedAtDesc();
    long countByStatus(LeaveStatus status);
}
