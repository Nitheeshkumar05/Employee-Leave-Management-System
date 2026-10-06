package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.EmployeeRequest;
import com.example.employeemanagement.exception.*;
import com.example.employeemanagement.model.*;
import com.example.employeemanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;

@Service @RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employees; private final DepartmentRepository departments; private final PasswordEncoder encoder;
    public List<Employee> findAll(String search){return search==null||search.isBlank()?employees.findAll():employees.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(search,search);}
    public Employee findById(Long id){return employees.findById(id).orElseThrow(()->new ResourceNotFoundException("Employee not found"));}
    public Employee findByEmail(String email){return employees.findByEmailIgnoreCase(email).orElseThrow(()->new ResourceNotFoundException("Employee not found"));}
    public Employee create(EmployeeRequest r){if(employees.existsByEmailIgnoreCase(r.email()))throw new BadRequestException("Email already exists"); Employee e=new Employee();apply(e,r,true);return employees.save(e);}
    public Employee update(Long id,EmployeeRequest r){Employee e=findById(id); if(!e.getEmail().equalsIgnoreCase(r.email())&&employees.existsByEmailIgnoreCase(r.email()))throw new BadRequestException("Email already exists"); apply(e,r,false); return employees.save(e);}
    public void delete(Long id){employees.delete(findById(id));}
    private void apply(Employee e,EmployeeRequest r,boolean create){e.setName(r.name().trim());e.setEmail(r.email().trim().toLowerCase());e.setPhone(r.phone().trim());e.setRole(r.role()==null?EmployeeRole.EMPLOYEE:r.role()); if(create||r.password()!=null&&!r.password().isBlank())e.setPassword(encoder.encode(create&&r.password()==null?"welcome123":r.password())); if(r.departmentId()!=null)e.setDepartment(departments.findById(r.departmentId()).orElseThrow(()->new ResourceNotFoundException("Department not found")));else e.setDepartment(null);}
}
