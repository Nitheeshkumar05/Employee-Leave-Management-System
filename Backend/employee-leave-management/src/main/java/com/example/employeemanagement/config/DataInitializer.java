package com.example.employeemanagement.config;

import com.example.employeemanagement.model.*;
import com.example.employeemanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration @RequiredArgsConstructor
public class DataInitializer {
    @Bean CommandLineRunner seedData(EmployeeRepository employees,DepartmentRepository departments,PasswordEncoder encoder){
        return args->{
            Department engineering=departments.findAll().stream().filter(d->d.getName().equalsIgnoreCase("Engineering")).findFirst().orElseGet(()->departments.save(Department.builder().name("Engineering").build()));
            if(!employees.existsByEmailIgnoreCase("hr@abccorp.com")) employees.save(Employee.builder().name("ABC HR").email("hr@abccorp.com").phone("9876543210").password(encoder.encode("hr123456")).role(EmployeeRole.HR).department(engineering).build());
        };
    }
}
