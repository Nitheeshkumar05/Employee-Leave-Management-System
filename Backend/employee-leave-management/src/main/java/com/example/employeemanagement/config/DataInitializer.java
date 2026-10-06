package com.example.employeemanagement.config;

import com.example.employeemanagement.model.*;
import com.example.employeemanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final EmployeeRepository employees;
    private final DepartmentRepository departments;
    private final LeaveRequestRepository leaves;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (departments.count() == 0) {
            Department eng = departments.save(new Department(null, "Engineering"));
            Department hrDept = departments.save(new Department(null, "Human Resources"));
            Department mkt = departments.save(new Department(null, "Marketing"));
            Department sales = departments.save(new Department(null, "Sales"));

            Employee hr = employees.save(Employee.builder().name("Sarah Jenkins (HR)").email("hr@abccorp.com")
                    .phone("555-0100").password(encoder.encode("hr123456")).role(EmployeeRole.HR).department(hrDept).build());

            Employee dev1 = employees.save(Employee.builder().name("John Doe").email("john@abccorp.com")
                    .phone("555-0101").password(encoder.encode("password")).role(EmployeeRole.EMPLOYEE).department(eng).build());

            Employee dev2 = employees.save(Employee.builder().name("Emily Chen").email("emily@abccorp.com")
                    .phone("555-0102").password(encoder.encode("password")).role(EmployeeRole.EMPLOYEE).department(eng).build());

            Employee marketer = employees.save(Employee.builder().name("Michael Scott").email("michael@abccorp.com")
                    .phone("555-0103").password(encoder.encode("password")).role(EmployeeRole.EMPLOYEE).department(mkt).build());

            // Seed some leaves
            leaves.save(LeaveRequest.builder().employee(dev1).leaveType(LeaveType.ANNUAL).startDate(LocalDate.now().plusDays(5)).endDate(LocalDate.now().plusDays(10)).reason("Family vacation to Hawaii").status(LeaveStatus.PENDING).build());
            leaves.save(LeaveRequest.builder().employee(dev2).leaveType(LeaveType.SICK).startDate(LocalDate.now().minusDays(2)).endDate(LocalDate.now().minusDays(1)).reason("Flu").status(LeaveStatus.APPROVED).hrNote("Hope you feel better!").reviewedBy(hr).reviewedAt(java.time.LocalDateTime.now()).build());
            leaves.save(LeaveRequest.builder().employee(marketer).leaveType(LeaveType.CASUAL).startDate(LocalDate.now().plusDays(15)).endDate(LocalDate.now().plusDays(16)).reason("Moving apartments").status(LeaveStatus.PENDING).build());
            
            System.out.println("Data Seeding Complete!");
        }
    }
}

