package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.*; import com.example.employeemanagement.model.*; import com.example.employeemanagement.repository.EmployeeRepository; import com.example.employeemanagement.security.JwtService; import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.http.HttpStatus; import org.springframework.security.authentication.*; import org.springframework.security.core.userdetails.UserDetails; import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/auth") @RequiredArgsConstructor
public class AuthController {
    private final EmployeeRepository employees; private final AuthenticationManager authenticationManager; private final JwtService jwtService; private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest r){if(employees.existsByEmailIgnoreCase(r.email()))throw new IllegalArgumentException("Email already exists"); Employee e=Employee.builder().name(r.name().trim()).email(r.email().trim().toLowerCase()).phone(r.phone().trim()).password(passwordEncoder.encode(r.password())).role(EmployeeRole.EMPLOYEE).build();return response(employees.save(e),null);}
    @PostMapping("/login") public AuthResponse login(@Valid @RequestBody LoginRequest r){var auth=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(r.email().trim().toLowerCase(),r.password())); UserDetails user=(UserDetails)auth.getPrincipal(); Employee e=employees.findByEmailIgnoreCase(user.getUsername()).orElseThrow(()->new BadCredentialsException("Invalid credentials")); return response(e,jwtService.generateToken(user));}
    private AuthResponse response(Employee e,String token){return new AuthResponse(e.getId(),e.getName(),e.getEmail(),e.getPhone(),e.getRole().name(),e.getDepartment()==null?"Unassigned":e.getDepartment().getName(),token);}
}
